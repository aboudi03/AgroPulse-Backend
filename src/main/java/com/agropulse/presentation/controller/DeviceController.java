package com.agropulse.presentation.controller;

import com.agropulse.domain.model.Device;
import com.agropulse.domain.repository.DeviceRepository;
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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        Map<String, String> response = new HashMap<>();

        if (request.getDeviceId() == null || request.getDeviceId().isEmpty()) {
            response.put("error", "deviceId is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getIp() == null || request.getIp().isEmpty()) {
            response.put("error", "ip is required");
            return ResponseEntity.badRequest().body(response);
        }

        Device device = deviceRepository.findById(request.getDeviceId())
                .orElse(new Device(request.getDeviceId(), request.getIp()));

        device.setIpAddress(request.getIp());
        deviceRepository.save(device);

        response.put("message", "Device registered successfully");
        response.put("deviceId", device.getDeviceId());
        response.put("ip", device.getIpAddress());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices(jakarta.servlet.http.HttpServletRequest request) {
        Long farmId = (Long) request.getAttribute("farmId");

        if (farmId == null) {
            return ResponseEntity.status(403).build();
        }

        List<Device> devices = deviceRepository.findByFarmId(farmId);
        List<DeviceDTO> deviceDTOs = devices.stream()
                .map(device -> new DeviceDTO(device.getDeviceId(), device.getIpAddress()))
                .toList();
        return ResponseEntity.ok(deviceDTOs);
    }

    @PostMapping("/{deviceId}/trigger")
    public ResponseEntity<Void> triggerDevice(@PathVariable String deviceId) {
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }
        device.setPendingUpdate(true);
        deviceRepository.save(device);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{deviceId}/command")
    public ResponseEntity<Map<String, String>> getDeviceCommand(@PathVariable String deviceId) {
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        if (device.isPendingUpdate()) {
            response.put("command", "UPDATE");
            // Reset flag
            device.setPendingUpdate(false);
            deviceRepository.save(device);
        } else {
            response.put("command", "NONE");
        }
        return ResponseEntity.ok(response);
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
