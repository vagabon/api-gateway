package com.auth.domain.user.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void givenUser_whenUpdateAvatar_thenAvatarIsUpdated() {
        var user = new User();
        user.setAvatar("avatar.png");

        user.updateUserAvatar("avatar2.jpg");

        assertThat(user.getAvatar()).matches("avatar2.jpg");
    }
}
