package com.base.infrastructure.controller;

import com.base.application.CreateUsecase;
import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseCrudDomain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateControllerTest {

    @Mock
    CreateUsecase<Profile> createUsecase;

    private class Profile extends BaseCrudDomain {
    }

    private class ProfileCreateController implements CreateController<Profile> {

        @Override
        public CreateUsecase<Profile> getCreateUsecase() {
            return createUsecase;
        }
    }

    private CreateController<Profile> controller() {
        return new ProfileCreateController();
    }

    @Test
    void create_shouldReturn200WithCreatedEntity() {
        var request = new Profile();
        var created = new Profile();
        when(createUsecase.execute(request)).thenReturn(created);

        var response = controller().create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(created);
        verify(createUsecase).execute(request);
    }

    @Test
    void create_shouldPropagateException_whenUsecaseFails() {
        var request = new Profile();
        when(createUsecase.execute(request)).thenThrow(new MetierException("ERROR"));

        var controller = controller();

        assertThatThrownBy(() -> controller.create(request))
            .isInstanceOf(MetierException.class);
    }
}