package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JpaDeviceRepository extends MongoRepository<Device, String> {
    List<Device> findByFarmId(Long farmId);

    List<Device> findByFarmIdIsNull();
}
