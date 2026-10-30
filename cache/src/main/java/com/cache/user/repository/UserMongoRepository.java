package com.cache.user.repository;

import java.util.Optional;

import com.cache.user.document.UserMongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserMongo, Long> {

    Optional<UserMongo> findByUsername(String username);
}
