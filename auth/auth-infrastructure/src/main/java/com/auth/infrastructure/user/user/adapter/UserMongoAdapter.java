package com.auth.infrastructure.user.user.adapter;

import java.util.Optional;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.model.UserLight;
import com.auth.domain.user.user.port.UserCacheRepository;
import com.auth.infrastructure.user.user.mapper.UserMapper;
import com.auth.infrastructure.user.user.mongo.document.UserMongoDocument;
import com.auth.infrastructure.user.user.mongo.repository.UserMongoRepository;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMongoAdapter implements UserCacheRepository {

    private final UserMongoRepository repository;
    private final UserMapper mapper;

    public Optional<UserMongoDocument> findById(Long id) {
        return repository.findById(id);
    }

    public UserLight save(User user) {
        var userToSave = findById(user.getId()).orElse(new UserMongoDocument());

        if (userToSave.getId() == null) {
            userToSave.setId(user.getId());
            userToSave.setUsername(user.getUsername());
            userToSave.setEmail(user.getEmail());
            userToSave.setCreationDate(user.getCreationDate());
        }
        userToSave.setAvatar(user.getAvatar());
        userToSave.setLastConnexionDate(user.getLastConnexionDate());

        var useMongo = repository.save(userToSave);
        return mapper.toDomainLight(useMongo);
    }

    public void evict(Long id) {
        repository.deleteById(id);
    }
}