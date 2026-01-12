package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.Farm;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JpaFarmRepository extends MongoRepository<Farm, Long> {
}
