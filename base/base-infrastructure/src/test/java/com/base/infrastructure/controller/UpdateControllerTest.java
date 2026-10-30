package com.base.infrastructure.controller;

import com.base.application.UpdateUsecase;
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
class UpdateControllerTest {

    @Mock
    UpdateUsecase<Profile> updateUsecase;

    private class Profile extends BaseCrudDomain {
    }

    private class ProfileUpdateController implements UpdateController<Profile> {

        @Override
        public UpdateUsecase<Profile> getUpdateUsecase() {
            return updateUsecase;
        }
    }

    private UpdateController<Profile> controller() {
        return new ProfileUpdateController();
    }

    @Test
    void update_shouldReturn200WithUpdatedEntity() {
        var request = new Profile();
        var updated = new Profile();
        when(updateUsecase.execute(request)).thenReturn(updated);

        var response = controller().update(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(updated);
        verify(updateUsecase).execute(request);
    }

    @Test
    void update_shouldPropagateException_whenUsecaseFails() {
        var request = new Profile();
        when(updateUsecase.execute(request)).thenThrow(new MetierException("ERROR"));

        var controller = controller();

        assertThatThrownBy(() -> controller.update(request))
            .isInstanceOf(MetierException.class);
    }
}