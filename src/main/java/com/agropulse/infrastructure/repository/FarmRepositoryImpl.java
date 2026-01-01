package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.Farm;
import com.agropulse.domain.repository.FarmRepository;
import com.agropulse.infrastructure.persistence.MongoFarmRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FarmRepositoryImpl implements FarmRepository {

    private final MongoFarmRepository mongo;

    public FarmRepositoryImpl(MongoFarmRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Farm save(Farm farm) {
        return mongo.save(farm);
    }

    @Override
    public Optional<Farm> findById(String id) {
        return mongo.findById(id);
    }

    @Override
    public List<Farm> findAll() {
        return mongo.findAll();
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
