package com.agropulse.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private String deviceId;

    private String ipAddress;

    @Column(name = "farm_id")
    private Long farmId;

    private LocalDateTime lastSeen;

    public Device() {
        this.lastSeen = LocalDateTime.now();
    }

    public Device(String deviceId, String ipAddress) {
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.lastSeen = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
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
}
