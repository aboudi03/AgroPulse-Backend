package com.agropulse.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "sensor_readings")
public class SensorReading {

    @Id
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

    public void setTimestampOnCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getSoil() {
        return soil;
    }

    public void setSoil(Integer soil) {
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
