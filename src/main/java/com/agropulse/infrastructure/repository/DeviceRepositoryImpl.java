package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.Device;
import com.agropulse.domain.repository.DeviceRepository;
import com.agropulse.infrastructure.persistence.MongoDeviceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {

    private final MongoDeviceRepository mongo;

    public DeviceRepositoryImpl(MongoDeviceRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Device save(Device device) {
        return mongo.save(device);
    }

    @Override
    public Optional<Device> findById(String deviceId) {
        return mongo.findById(deviceId);
    }

    @Override
    public List<Device> findAll() {
        return mongo.findAll();
    }

    @Override
    public List<Device> findByFarmId(String farmId) {
        // MongoDB repository will need a custom query method
        return mongo.findAll().stream()
                .filter(d -> farmId.equals(d.getFarmId()))
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongo.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongo.existsById(id);
    }
}
