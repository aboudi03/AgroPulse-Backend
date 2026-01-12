package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.SensorReading;
import com.agropulse.domain.repository.SensorRepository;
import com.agropulse.infrastructure.persistence.JpaSensorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SensorRepositoryImpl implements SensorRepository {

    private final JpaSensorRepository jpa;

    public SensorRepositoryImpl(JpaSensorRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public SensorReading save(SensorReading reading) {
        return jpa.save(reading);
    }

    @Override
    public SensorReading findLatest() {
        return jpa.findFirstByOrderByTimestampDesc().orElse(null);
    }

    @Override
    public List<SensorReading> findAll() {
        return jpa.findAll();
    }

    @Override
    public List<SensorReading> findByDeviceId(String deviceId) {
        return jpa.findByDeviceId(deviceId);
    }

    @Override
    public SensorReading findLatestByDeviceId(String deviceId) {
        return jpa.findFirstByDeviceIdOrderByTimestampDesc(deviceId).orElse(null);
    }
}
