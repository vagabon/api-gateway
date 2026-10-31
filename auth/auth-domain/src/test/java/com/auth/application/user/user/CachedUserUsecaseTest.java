package com.auth.application.user.user;

import com.auth.domain.user.user.model.User;
import com.auth.domain.user.user.model.UserLight;
import com.auth.domain.user.user.port.UserCacheRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CachedUserUsecaseTest {

    @Mock
    UserCacheRepository userCacheRepository;

    @InjectMocks
    CachedUserUsecase usecase;

    @Test
    void execute_shouldCreateUserWithGoogleFieldsAndDefaultRole() {
        var user = new User();
        var userLight = new UserLight();
        when(userCacheRepository.save(user)).thenReturn(userLight);

        var tested = usecase.execute(user);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userCacheRepository).save(captor.capture());
        assertThat(tested).isSameAs(userLight);
    }
}
