package com.agropulse.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private Integer soil;
    private Double humidity;
    private Double temperature;

    private boolean urgent = false;

    private LocalDateTime timestamp;

    public SensorReading() {
    }

    public SensorReading(Integer soil, Double humidity, Double temperature, boolean urgent) {
        this.soil = soil;
        this.humidity = humidity;
        this.temperature = temperature;
        this.urgent = urgent;
    }

    @PrePersist
    public void setTimestampOnCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getSoil() { // <-- FIXED
        return soil;
    }

    public void setSoil(Integer soil) { // <-- FIXED
        this.soil = soil;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
