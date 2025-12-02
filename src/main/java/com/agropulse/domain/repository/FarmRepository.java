package com.agropulse.domain.repository;

import com.agropulse.domain.model.Farm;
import java.util.List;
import java.util.Optional;

public interface FarmRepository {
    Farm save(Farm farm);

    Optional<Farm> findById(Long id);

    List<Farm> findAll();
}
