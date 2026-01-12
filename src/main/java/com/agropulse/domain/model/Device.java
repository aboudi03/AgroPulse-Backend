package com.agropulse.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "devices")
public class Device {

    @Id
    private String deviceId;

    private String ipAddress;

    private Long farmId;

    private LocalDateTime lastSeen;

    private boolean pendingUpdate = false;

    public Device() {
        this.lastSeen = LocalDateTime.now();
    }

    public Device(String deviceId, String ipAddress) {
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.lastSeen = LocalDateTime.now();
    }

    public void updateLastSeen() {
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

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public boolean isPendingUpdate() {
        return pendingUpdate;
    }

    public void setPendingUpdate(boolean pendingUpdate) {
        this.pendingUpdate = pendingUpdate;
    }
}
