package com.auth.exposition.rest.user.controller;

import com.auth.domain.user.user.model.User;
import com.base.application.CreateUsecase;
import com.base.application.FindByIdUsecase;
import com.base.application.FindByUsecase;
import com.base.application.UpdateUsecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    FindByUsecase<User> findByUsecase;
    @Mock
    FindByIdUsecase<User> findByIdUsecase;
    @Mock
    CreateUsecase<User> createUsecase;
    @Mock
    UpdateUsecase<User> updateUsecase;

    @InjectMocks
    UserRestController controller;

    @Test
    void me_shouldReturn200_whenUserHasRoleUser() {
        var user = new User();
        var jwt = Mockito.mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("42");
        when(findByIdUsecase.execute(42L)).thenReturn(user);

        assertThat(controller.me(jwt)).isSameAs(user);
    }
}