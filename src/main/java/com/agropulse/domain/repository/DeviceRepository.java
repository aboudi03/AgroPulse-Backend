package com.agropulse.domain.repository;

import com.agropulse.domain.model.Device;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository {
    Device save(Device device);

    Optional<Device> findById(String deviceId);

    List<Device> findAll();

    List<Device> findByFarmId(String farmId);

    void deleteById(String id);

    boolean existsById(String id);
}
