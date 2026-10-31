package com.auth.infrastructure.user.user.mongo.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "users")
@Getter
@Setter
public class UserMongoDocument {

    @Id
    private Long id;

    @Indexed(unique = true)
    private String username;

    private String email;
    private String avatar;
    private Instant creationDate;

    private Instant lastConnexionDate;
}