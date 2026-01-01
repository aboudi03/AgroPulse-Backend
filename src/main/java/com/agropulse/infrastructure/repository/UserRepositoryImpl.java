package com.agropulse.infrastructure.repository;

import com.agropulse.domain.model.User;
import com.agropulse.domain.repository.UserRepository;
import com.agropulse.infrastructure.persistence.MongoUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final MongoUserRepository mongo;

    public UserRepositoryImpl(MongoUserRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public User save(User user) {
        return mongo.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return mongo.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongo.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
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
