package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.Farm;
import com.agropulse.domain.repository.FarmRepository;
import com.agropulse.infrastructure.persistence.JpaFarmRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FarmRepositoryImpl implements FarmRepository {

    private final JpaFarmRepository jpa;

    public FarmRepositoryImpl(JpaFarmRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Farm save(Farm farm) {
        return jpa.save(farm);
    }

    @Override
    public Optional<Farm> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Farm> findAll() {
        return jpa.findAll();
    }
}
