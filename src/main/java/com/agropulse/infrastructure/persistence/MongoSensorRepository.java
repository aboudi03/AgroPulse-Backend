package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.SensorReading;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface MongoSensorRepository extends MongoRepository<SensorReading, String> {

    @Query(value = "{}", sort = "{ 'timestamp': -1 }")
    SensorReading findTopByOrderByTimestampDesc();

    List<SensorReading> findByDeviceIdOrderByTimestampDesc(String deviceId);

    SensorReading findTopByDeviceIdOrderByTimestampDesc(String deviceId);
}
