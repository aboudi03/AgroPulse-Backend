package com.agropulse.infrastructure.persistence;

import com.agropulse.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface JpaUserRepository extends MongoRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
