package com.agropulse.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "devices")
public class Device {

    @Id
    private String deviceId;

    private String ipAddress;

    private String farmId;

    private String userId;

    private LocalDateTime lastSeen;

    public Device() {
        this.lastSeen = LocalDateTime.now();
    }

    public Device(String deviceId, String ipAddress) {
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.lastSeen = LocalDateTime.now();
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

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
