package com.agropulse.domain.repository;

import com.agropulse.domain.model.SensorReading;
import java.util.List;

public interface SensorRepository {

    SensorReading save(SensorReading reading);

    SensorReading findLatest();

    List<SensorReading> findAll();

    List<SensorReading> findByDeviceId(String deviceId);

    SensorReading findLatestByDeviceId(String deviceId);
}
