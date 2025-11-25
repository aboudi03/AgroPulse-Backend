package com.agropulse.presentation.controller;

import com.agropulse.application.service.SensorService;
import com.agropulse.domain.model.SensorReading;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<Map<String, String>> triggerEsp32(@PathVariable String deviceId) {
        Map<String, String> body = new HashMap<>();

        return deviceRepository.findById(deviceId)
                .map(device -> {
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

    @PostMapping("/add")
    public ResponseEntity<SensorReading> addReading(@RequestBody SensorReading reading,
            jakarta.servlet.http.HttpServletRequest request) {
        // Save/Update Device IP
        if (reading.getDeviceId() != null) {
            String ip = request.getRemoteAddr();
            com.agropulse.domain.model.Device device = deviceRepository.findById(reading.getDeviceId())
                    .orElse(new com.agropulse.domain.model.Device(reading.getDeviceId(), ip));
            device.setIpAddress(ip); // Update IP in case it changed
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
    public ResponseEntity<SensorReading> getLatestByDevice(@PathVariable String deviceId) {
        SensorReading latest = service.getLatestReading(deviceId);
        if (latest == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(latest);
    }

    @GetMapping("/{deviceId}/all")
    public ResponseEntity<List<SensorReading>> getAllByDevice(@PathVariable String deviceId) {
        return ResponseEntity.ok(service.getAllReadings(deviceId));
    }
}
