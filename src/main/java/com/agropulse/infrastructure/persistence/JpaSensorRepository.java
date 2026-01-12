package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.SensorReading;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface JpaSensorRepository extends MongoRepository<SensorReading, Long> {

    Optional<SensorReading> findFirstByOrderByTimestampDesc();

    List<SensorReading> findByDeviceId(String deviceId);

    Optional<SensorReading> findFirstByDeviceIdOrderByTimestampDesc(String deviceId);
}
