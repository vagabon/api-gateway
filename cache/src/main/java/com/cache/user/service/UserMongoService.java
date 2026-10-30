package com.cache.user.service;

import java.util.Optional;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;
import com.cache.user.document.UserMongo;
import com.cache.user.repository.UserMongoRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMongoService {

    private final UserMongoRepository repository;

    public Optional<UserMongo> findById(Long id) {
        return repository.findById(id);
    }

    @SuppressWarnings("null")
    public UserMongo save(User user) {
        var userToSave = findById(user.getId()).orElse(null);

        if (userToSave == null) {
            userToSave = new UserMongo();
            userToSave.setId(user.getId());
            userToSave.setUsername(user.getUsername());
            userToSave.setEmail(user.getEmail());
            userToSave.setCreationDate(user.getCreationDate());
        }
        userToSave.setAvatar(user.getAvatar());
        userToSave.setLastConnexionDate(user.getLastConnexionDate());
        userToSave.setProfiles(
            user.getProfiles().stream().map(Profile::getName).toList());

        return repository.save(userToSave);
    }

    public void evict(Long id) {
        repository.deleteById(id);
    }
}