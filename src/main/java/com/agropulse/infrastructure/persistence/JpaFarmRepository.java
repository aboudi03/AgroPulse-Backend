package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFarmRepository extends JpaRepository<Farm, Long> {
}
