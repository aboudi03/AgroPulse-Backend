package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaSensorRepository extends JpaRepository<SensorReading, Long> {

    @Query("SELECT r FROM SensorReading r ORDER BY r.timestamp DESC LIMIT 1")
    SensorReading findLatest();
}
