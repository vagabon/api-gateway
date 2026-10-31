package com.base.domain.model.currentuser;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserTest {

    @Test
    void givenCurrentUser_whenInit_thenValueAreCorrect() {
        var roles = Set.of("ADMIN");
        var currentUser = new CurrentUser(1L, "vagabond", "vagabond@example.com", roles);

        assertThat(currentUser.id()).isEqualTo(1L);
        assertThat(currentUser.username()).isEqualTo("vagabond");
        assertThat(currentUser.email()).isEqualTo("vagabond@example.com");
        assertThat(currentUser.roles()).isEqualTo(roles);
    }
}
