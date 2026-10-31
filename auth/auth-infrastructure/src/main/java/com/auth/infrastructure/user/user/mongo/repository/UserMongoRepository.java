package com.auth.infrastructure.user.user.mongo.repository;

import java.util.Optional;

import com.auth.infrastructure.user.user.mongo.document.UserMongoDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserMongoDocument, Long> {

    Optional<UserMongoDocument> findByUsername(String username);
}
