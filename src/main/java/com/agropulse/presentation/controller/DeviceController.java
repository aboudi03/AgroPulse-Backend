package com.agropulse.presentation.controller;

import com.agropulse.domain.model.Device;
import com.agropulse.domain.repository.DeviceRepository;
import com.agropulse.infrastructure.util.IpAddressUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Register a new ESP32 device.
     * IP address is optional - it will be automatically detected from the HTTP request if not provided.
     * This allows ESP32 to use DHCP - no static IP configuration needed!
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerDevice(
            @RequestBody DeviceRegistrationRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        Map<String, String> response = new HashMap<>();

        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            response.put("error", "deviceId is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Auto-detect IP from request if not provided
        String ipAddress = request.getIp();
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = IpAddressUtil.getClientIpAddress(httpRequest);
        }

        boolean isNewDevice = !deviceRepository.existsById(request.getDeviceId());
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElse(new Device(request.getDeviceId(), ipAddress));

        // Update IP address (in case it changed)
        device.setIpAddress(ipAddress);
        // For new devices, ensure farmId is null - devices should be assigned manually via admin panel
        // For existing devices, preserve the current farmId (don't overwrite it)
        if (isNewDevice) {
            device.setFarmId(null);
        }
        deviceRepository.save(device);

        response.put("message", "Device registered successfully");
        response.put("deviceId", device.getDeviceId());
        response.put("ip", device.getIpAddress());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices(jakarta.servlet.http.HttpServletRequest request) {
        String farmId = (String) request.getAttribute("farmId");

        if (farmId == null) {
            return ResponseEntity.status(403).build();
        }

        List<Device> devices = deviceRepository.findByFarmId(farmId);
        List<DeviceDTO> deviceDTOs = devices.stream()
                .map(device -> new DeviceDTO(device.getDeviceId(), device.getIpAddress()))
                .toList();
        return ResponseEntity.ok(deviceDTOs);
    }

    // DTO for registration request
    public static class DeviceRegistrationRequest {
        private String deviceId;
        private String ip;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    // DTO for device response
    public static class DeviceDTO {
        private String deviceId;
        private String ip;

        public DeviceDTO(String deviceId, String ip) {
            this.deviceId = deviceId;
            this.ip = ip;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }
}
