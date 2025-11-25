package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.Device;
import com.agropulse.domain.repository.DeviceRepository;
import com.agropulse.infrastructure.persistence.JpaDeviceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    private final JpaDeviceRepository jpa;

    public DeviceRepositoryImpl(JpaDeviceRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Device save(Device device) {
        return jpa.save(device);
    }

    @Override
    public Optional<Device> findById(String deviceId) {
        return jpa.findById(deviceId);
    }

    @Override
    public List<Device> findAll() {
        return jpa.findAll();
    }
}
