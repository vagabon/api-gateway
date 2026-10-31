package com.auth.application.auth;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.auth.application.user.refresh.CreateUserRefreshUsecase;
import com.auth.application.user.user.CreateUserWithGoogleUsecase;
import com.auth.domain.auth.model.AuthGoogle;
import com.auth.domain.auth.port.AuthCookieRepositoryPort;
import com.auth.domain.auth.port.AuthJwtRepositoryPort;
import com.auth.domain.role.model.Role;
import com.auth.domain.role.port.RoleRepositoryPort;
import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.port.UserRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUsecaseTest {

    private static final int TTL = 3600;

    @Mock
    UserRepositoryPort userRepository;
    @Mock
    RoleRepositoryPort roleRepository;
    @Mock
    AuthJwtRepositoryPort authJwtRepository;
    @Mock
    AuthCookieRepositoryPort authCookieRepository;
    @Mock
    CreateUserWithGoogleUsecase createUserWithGoogleUsecase;
    @Mock
    CreateUserRefreshUsecase createUserRefreshUsecase;

    private final String origin = "http://localhost:4200";
    private final String ipAdress = "127.0.0.1";

    private AuthenticateUsecase usecase;

    private final AuthGoogle google = new AuthGoogle("sub-1", "aud-1",
        "vagabond@example.com",
        true,
        "Vagabond",
        "avatar.png");

    @BeforeEach
    void setUp() {
        usecase = new AuthenticateUsecase(
            userRepository, roleRepository, authJwtRepository, authCookieRepository,
            createUserWithGoogleUsecase, createUserRefreshUsecase);
    }

    @Test
    void execute_shouldUpdateUserAndBuildIdentity_whenUserExists() {
        var existing = new User();
        existing.setAvatar("old-pic.png");
        existing.setRoles(new HashSet<>());
        when(userRepository.findByGoogleId("sub-1")).thenReturn(existing);
        when(roleRepository.getRoleForUser(origin)).thenReturn(new Role());
        stubTokens(existing);
        var before = Instant.now();

        var result = usecase.execute(google, origin, ipAdress);

        assertThat(result.user()).isSameAs(existing);
        assertThat(existing.getAvatar()).isEqualTo("avatar.png");
        assertThat(existing.getLastConnexionDate()).isBetween(before, Instant.now());
        verifyNoInteractions(createUserWithGoogleUsecase);
    }

    @Test
    void execute_shouldCreateUser_whenUserDoesNotExist() {
        var created = new User();
        when(userRepository.findByGoogleId("sub-1")).thenReturn(null);
        when(createUserWithGoogleUsecase.execute(google, origin))
            .thenReturn(created);
        stubTokens(created);

        var result = usecase.execute(google, origin, ipAdress);

        assertThat(result.user()).isSameAs(created);
        verify(createUserWithGoogleUsecase).execute(google, origin);
        verify(userRepository, never()).update(any());
    }

    @Test
    void execute_shouldGenerateRefreshThenJwtThenCookie_withSameUserAndToken() {
        var user = new User();
        var role = new Role();
        role.setName("name");
        role.setOrigin(origin);
        user.setRoles(Set.of(role));
        when(userRepository.findByGoogleId("sub-1")).thenReturn(user);
        stubTokens(user);

        usecase.execute(google, origin, ipAdress);

        var order = inOrder(createUserRefreshUsecase, authJwtRepository,
            authCookieRepository);
        order.verify(createUserRefreshUsecase).execute(user, origin, ipAdress, TTL);
        order.verify(authJwtRepository).generateTokenJwt(user, origin);
        order.verify(authCookieRepository).create("refresh-1");
    }

    // adapte les types (String ici) à ceux de tes ports
    private void stubTokens(User user) {
        when(authCookieRepository.getRefreshTokenTimeToLive()).thenReturn(TTL);
        when(createUserRefreshUsecase.execute(user, origin, ipAdress, TTL))
            .thenReturn("refresh-1");
        when(authJwtRepository.generateTokenJwt(user, origin)).thenReturn("jwt");
        when(authCookieRepository.create("refresh-1")).thenReturn("cookie");
    }
}