package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.SensorReading;
import com.agropulse.domain.repository.SensorRepository;
import com.agropulse.infrastructure.persistence.MongoSensorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SensorRepositoryImpl implements SensorRepository {

    private final MongoSensorRepository mongo;

    public SensorRepositoryImpl(MongoSensorRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public SensorReading save(SensorReading reading) {
        return mongo.save(reading);
    }

    @Override
    public SensorReading findLatest() {
        return mongo.findTopByOrderByTimestampDesc();
    }

    @Override
    public List<SensorReading> findAll() {
        return mongo.findAll();
    }

    @Override
    public List<SensorReading> findByDeviceId(String deviceId) {
        return mongo.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @Override
    public SensorReading findLatestByDeviceId(String deviceId) {
        return mongo.findTopByDeviceIdOrderByTimestampDesc(deviceId);
    }
}
