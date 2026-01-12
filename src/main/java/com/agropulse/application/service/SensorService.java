package com.agropulse.application.service;

import com.agropulse.domain.repository.SensorRepository;
import com.agropulse.domain.model.SensorReading;
import com.agropulse.infrastructure.persistence.SequenceGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorService {

    private final SensorRepository repository;
    private final SequenceGenerator sequenceGenerator;

    public SensorService(SensorRepository repository, SequenceGenerator sequenceGenerator) {
        this.repository = repository;
        this.sequenceGenerator = sequenceGenerator;
    }

    public SensorReading saveReading(SensorReading reading) {
        if (reading.getDeviceId() == null || reading.getDeviceId().isEmpty()) {
            throw new IllegalArgumentException("Device ID is required");
        }

        if (reading.getId() == null) {
            reading.setId(sequenceGenerator.getNextSequenceId("sensor_readings"));
        }

        if (reading.getTimestamp() == null) {
            reading.setTimestamp(LocalDateTime.now());
        }

        if (!reading.isUrgent()) {
            boolean urgent = false;

            if (reading.getSoil() != null && reading.getSoil() < 20) {
                urgent = true;
            }
            if (reading.getTemperature() != null && reading.getTemperature() > 35) {
                urgent = true;
            }

            reading.setUrgent(urgent);
        }

        return repository.save(reading);
    }

    public SensorReading getLatestReading() {
        return repository.findLatest();
    }

    public List<SensorReading> getAllReadings() {
        return repository.findAll();
    }

    public SensorReading getLatestReading(String deviceId) {
        return repository.findLatestByDeviceId(deviceId);
    }

    public List<SensorReading> getAllReadings(String deviceId) {
        return repository.findByDeviceId(deviceId);
    }
}
