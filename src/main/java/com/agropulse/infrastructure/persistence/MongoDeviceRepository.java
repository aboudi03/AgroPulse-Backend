package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDeviceRepository extends MongoRepository<Device, String> {
}
