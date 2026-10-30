package com.cache.user.document;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "users")
@Getter
@Setter
public class UserMongo {

    @Id
    private Long id;

    @Indexed(unique = true)
    private String username;

    private String email;
    private String avatar;
    private Instant creationDate;

    @Indexed(expireAfter = "30d")
    private Instant lastConnexionDate;

    private List<String> profiles;

}