package com.agropulse.domain.repository;

import com.agropulse.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}
