package com.agropulse.domain.repository;

import com.agropulse.domain.model.Farm;
import java.util.List;
import java.util.Optional;

public interface FarmRepository {
    Farm save(Farm farm);

    Optional<Farm> findById(String id);

    List<Farm> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
