package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaDeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByFarmId(Long farmId);
}
