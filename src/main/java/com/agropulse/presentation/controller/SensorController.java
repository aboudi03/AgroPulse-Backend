package com.agropulse.presentation.controller;

import com.agropulse.application.service.SensorService;
import com.agropulse.domain.model.Device;
import com.agropulse.domain.model.SensorReading;
import com.agropulse.infrastructure.util.IpAddressUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    private final SensorService service;
    private final com.agropulse.domain.repository.DeviceRepository deviceRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public SensorController(SensorService service, com.agropulse.domain.repository.DeviceRepository deviceRepository) {
        this.service = service;
        this.deviceRepository = deviceRepository;
    }

    // ================================
    // 🔥 Trigger ESP32 and return JSON
    // ================================
    @PostMapping("/trigger/{deviceId}")
    public ResponseEntity<Map<String, String>> triggerEsp32(@PathVariable String deviceId,
            jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> body = new HashMap<>();
        Long farmId = (Long) request.getAttribute("farmId");

        return deviceRepository.findById(deviceId)
                .map(device -> {
                    // Security Check: Device must belong to user's farm
                    if (farmId != null && !farmId.equals(device.getFarmId())) {
                        body.put("error", "Access denied: Device does not belong to your farm");
                        return ResponseEntity.status(403).body(body);
                    }

                    String ip = device.getIpAddress();
                    if (ip == null || ip.isEmpty()) {
                        body.put("error", "Device IP not found. Ensure device has sent data recently.");
                        return ResponseEntity.status(404).body(body);
                    }

                    String url = "http://" + ip + "/trigger";
                    try {
                        String response = restTemplate.getForObject(url, String.class);
                        body.put("message", "Triggered " + deviceId + " at " + ip + ": " + response);
                        return ResponseEntity.ok(body);
                    } catch (Exception e) {
                        body.put("error", "Failed to contact " + deviceId + " at " + ip + ": " + e.getMessage());
                        return ResponseEntity.status(500).body(body);
                    }
                })
                .orElseGet(() -> {
                    body.put("error", "Device not found: " + deviceId);
                    return ResponseEntity.status(404).body(body);
                });
    }

    /**
     * ESP32 sends sensor data here.
     * Automatically extracts and saves the device's IP address from the HTTP request.
     * This allows ESP32 to use DHCP - no static IP configuration needed!
     */
    @PostMapping("/add")
    public ResponseEntity<SensorReading> addReading(@RequestBody SensorReading reading,
            jakarta.servlet.http.HttpServletRequest request) {
        // Automatically extract and save device IP from the HTTP request
        if (reading.getDeviceId() != null && !reading.getDeviceId().isEmpty()) {
            // Extract real client IP (handles proxies, load balancers, etc.)
            String clientIp = IpAddressUtil.getClientIpAddress(request);
            
            // Find existing device or create new one
            Device device = deviceRepository.findById(reading.getDeviceId())
                    .orElse(new Device(reading.getDeviceId(), clientIp));
            
            // Update IP address (in case it changed due to DHCP renewal)
            device.setIpAddress(clientIp);
            // lastSeen is automatically updated by @PreUpdate in Device entity
            
            deviceRepository.save(device);
        }

        SensorReading saved = service.saveReading(reading);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorReading> getLatest() {
        SensorReading latest = service.getLatestReading();
        if (latest == null)
            return ResponseEntity.noContent().build();

        if (latest.getSoil() == null &&
                latest.getHumidity() == null &&
                latest.getTemperature() == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(latest);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorReading>> getAll() {
        return ResponseEntity.ok(service.getAllReadings());
    }

    @GetMapping("/{deviceId}/latest")
    public ResponseEntity<SensorReading> getLatestByDevice(@PathVariable String deviceId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long farmId = (Long) request.getAttribute("farmId");

        // Check if device belongs to farm
        if (farmId != null) {
            com.agropulse.domain.model.Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null || !farmId.equals(device.getFarmId())) {
                return ResponseEntity.status(403).build();
            }
        }

        SensorReading latest = service.getLatestReading(deviceId);
        if (latest == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/{deviceId}/all")
    public ResponseEntity<List<SensorReading>> getAllByDevice(@PathVariable String deviceId,
            jakarta.servlet.http.HttpServletRequest request) {
        Long farmId = (Long) request.getAttribute("farmId");

        // Check if device belongs to farm
        if (farmId != null) {
            com.agropulse.domain.model.Device device = deviceRepository.findById(deviceId).orElse(null);
            if (device == null || !farmId.equals(device.getFarmId())) {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(service.getAllReadings(deviceId));
    }
}
