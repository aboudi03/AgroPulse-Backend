package com.agropulse.presentation.controller;

import com.agropulse.application.service.AuthService;
import com.agropulse.domain.model.Device;
import com.agropulse.domain.model.Farm;
import com.agropulse.domain.model.User;
import com.agropulse.domain.repository.DeviceRepository;
import com.agropulse.domain.repository.FarmRepository;
import com.agropulse.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final AuthService authService;

    public AdminController(FarmRepository farmRepository, UserRepository userRepository,
            DeviceRepository deviceRepository, AuthService authService) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.authService = authService;
    }

    // ==================== FARM MANAGEMENT ====================

    @PostMapping("/farms")
    public ResponseEntity<Map<String, Object>> createFarm(@RequestBody CreateFarmRequest request) {
        Map<String, Object> response = new HashMap<>();

        Farm farm = new Farm(request.getName());
        Farm saved = farmRepository.save(farm);

        response.put("message", "Farm created successfully");
        response.put("farmId", saved.getId());
        response.put("name", saved.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/farms")
    public ResponseEntity<List<FarmDTO>> getAllFarms() {
        List<Farm> farms = farmRepository.findAll();
        List<FarmDTO> farmDTOs = farms.stream()
                .map(f -> new FarmDTO(f.getId(), f.getName(), f.getCreatedAt().toString()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(farmDTOs);
    }

    @DeleteMapping("/farms/{farmId}")
    public ResponseEntity<Map<String, String>> deleteFarm(@PathVariable String farmId) {
        if (!farmRepository.existsById(farmId)) {
            return ResponseEntity.notFound().build();
        }
        farmRepository.deleteById(farmId);
        // Optional: Also delete or unassign users/devices associated with this farm
        Map<String, String> response = new HashMap<>();
        response.put("message", "Farm deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ==================== USER MANAGEMENT ====================

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody CreateUserRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = authService.register(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail(),
                    request.getFarmId(),
                    User.Role.valueOf(request.getRole()));

            response.put("message", "User created successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("farmId", user.getFarmId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getFarmId(), u.getRole().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String userId,
            @RequestBody UpdateUserRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getFarmId() != null) {
                user.setFarmId(request.getFarmId());
            }
            if (request.getRole() != null) {
                user.setRole(User.Role.valueOf(request.getRole()));
            }
            // Password update should ideally be handled separately or hashed here if
            // included

            userRepository.save(user);

            response.put("message", "User updated successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("farmId", user.getFarmId());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ==================== DEVICE MANAGEMENT ====================

    /**
     * Assign a device to a farm.
     * IP address is optional - it will be automatically detected when the device
     * sends sensor data.
     * ESP32 should use DHCP, and the backend will automatically read the IP from
     * incoming requests.
     */
    @PostMapping("/devices/assign")
    public ResponseEntity<Map<String, String>> assignDeviceToFarm(@RequestBody AssignDeviceRequest request) {
        Map<String, String> response = new HashMap<>();

        // Find existing device or create new one
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElse(new Device(request.getDeviceId(), null)); // IP will be auto-detected

        device.setFarmId(request.getFarmId());

        // IP is optional - only set if explicitly provided (for manual override)
        // Otherwise, IP will be automatically detected when ESP32 sends sensor data
        if (request.getIp() != null && !request.getIp().isEmpty()) {
            device.setIpAddress(request.getIp());
        }

        deviceRepository.save(device);

        response.put("message", "Device assigned to farm successfully");
        response.put("deviceId", device.getDeviceId());
        response.put("farmId", device.getFarmId());
        if (device.getIpAddress() != null) {
            response.put("ipAddress", device.getIpAddress());
        } else {
            response.put("ipAddress", "Will be auto-detected when device sends data");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/devices")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        List<DeviceDTO> deviceDTOs = devices.stream()
                .map(d -> new DeviceDTO(d.getDeviceId(), d.getIpAddress(), d.getFarmId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(deviceDTOs);
    }

    @PutMapping("/devices/{deviceId}")
    public ResponseEntity<Map<String, Object>> updateDevice(@PathVariable String deviceId,
            @RequestBody UpdateDeviceRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Device device = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> new RuntimeException("Device not found"));

            // Validate farm exists if farmId is being updated
            if (request.getFarmId() != null && !request.getFarmId().isEmpty()) {
                if (!farmRepository.existsById(request.getFarmId())) {
                    response.put("error", "Farm not found");
                    return ResponseEntity.badRequest().body(response);
                }
                device.setFarmId(request.getFarmId());
            }

            // Update IP address if provided
            if (request.getIp() != null && !request.getIp().isEmpty()) {
                device.setIpAddress(request.getIp());
            }

            deviceRepository.save(device);

            response.put("message", "Device updated successfully");
            response.put("deviceId", device.getDeviceId());
            response.put("farmId", device.getFarmId());
            response.put("ipAddress", device.getIpAddress());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<Map<String, String>> deleteDevice(@PathVariable String deviceId) {
        if (!deviceRepository.existsById(deviceId)) {
            return ResponseEntity.notFound().build();
        }
        deviceRepository.deleteById(deviceId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Device deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Scan network for ESP32 devices.
     * Returns all devices that have been seen (sent sensor data) with their
     * registration status.
     * Devices are automatically discovered when they send sensor data to
     * /api/sensor/add.
     */
    @PostMapping("/devices/scan")
    public ResponseEntity<List<ScannedEspDto>> scanEspDevices() {
        List<Device> allDevices = deviceRepository.findAll();

        // Convert to ScannedEspDto with registration status
        List<ScannedEspDto> scannedDevices = allDevices.stream()
                .map(device -> {
                    boolean isRegistered = device.getFarmId() != null;
                    return new ScannedEspDto(
                            device.getDeviceId(),
                            device.getIpAddress() != null ? device.getIpAddress() : "Unknown",
                            null, // MAC address not stored in Device entity
                            isRegistered,
                            device.getFarmId());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(scannedDevices);
    }

    // ==================== DTOs ====================

    public static class CreateFarmRequest {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CreateUserRequest {
        private String username;
        private String password;
        private String email;
        private String farmId;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFarmId() {
            return farmId;
        }

        public void setFarmId(String farmId) {
            this.farmId = farmId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class UpdateUserRequest {
        private String email;
        private String farmId;
        private String role;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFarmId() {
            return farmId;
        }

        public void setFarmId(String farmId) {
            this.farmId = farmId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class AssignDeviceRequest {
        private String deviceId;
        private String farmId;
        private String ip;

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getFarmId() {
            return farmId;
        }

        public void setFarmId(String farmId) {
            this.farmId = farmId;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    public static class UpdateDeviceRequest {
        private String farmId;
        private String ip;

        public String getFarmId() {
            return farmId;
        }

        public void setFarmId(String farmId) {
            this.farmId = farmId;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    public static class FarmDTO {
        private String id;
        private String name;
        private String createdAt;

        public FarmDTO(String id, String name, String createdAt) {
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }

    public static class UserDTO {
        private String id;
        private String username;
        private String email;
        private String farmId;
        private String role;

        public UserDTO(String id, String username, String email, String farmId, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.farmId = farmId;
            this.role = role;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getFarmId() {
            return farmId;
        }

        public String getRole() {
            return role;
        }
    }

    public static class DeviceDTO {
        private String deviceId;
        private String ip;
        private String farmId;

        public DeviceDTO(String deviceId, String ip, String farmId) {
            this.deviceId = deviceId;
            this.ip = ip;
            this.farmId = farmId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getIp() {
            return ip;
        }

        public String getFarmId() {
            return farmId;
        }
    }

    /**
     * DTO for scanned ESP32 devices.
     * Matches the frontend ScannedEspDto interface.
     */
    public static class ScannedEspDto {
        private String deviceId;
        private String ipAddress;
        private String macAddress; // Optional, may be null
        private boolean registered;
        private String farmId; // null if not registered

        public ScannedEspDto(String deviceId, String ipAddress, String macAddress, boolean registered, String farmId) {
            this.deviceId = deviceId;
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
            this.registered = registered;
            this.farmId = farmId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

        public String getFarmId() {
            return farmId;
        }

        public void setFarmId(String farmId) {
            this.farmId = farmId;
        }
    }
}
