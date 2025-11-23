package com.agropulse.application.service;

import com.agropulse.domain.repository.SensorRepository;
import com.agropulse.domain.model.SensorReading;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorService {

    private final SensorRepository repository;

    public SensorService(SensorRepository repository) {
        this.repository = repository;
    }

    public SensorReading saveReading(SensorReading reading) {
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
}
