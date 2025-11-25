package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDeviceRepository extends JpaRepository<Device, String> {
}
