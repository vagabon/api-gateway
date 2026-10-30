package com.base.infrastructure.controller;

import com.base.application.DesactivateUsecase;
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
class DesactivateControllerTest {

    @Mock
    DesactivateUsecase<Profile> desactivateUsecase;

    private class Profile extends BaseCrudDomain {
    }

    private class ProfileDesactivateController implements DesactivateController<Profile> {

        @Override
        public DesactivateUsecase<Profile> getDesactivateUsecase() {
            return desactivateUsecase;
        }
    }

    private DesactivateController<Profile> controller() {
        return new ProfileDesactivateController();
    }

    @Test
    void desactivate_shouldReturn200WithDesactivatedEntity() {
        var desactivated = new Profile();
        when(desactivateUsecase.execute(1L)).thenReturn(desactivated);

        var response = controller().desactivate(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(desactivated);
        verify(desactivateUsecase).execute(1L);
    }

    @Test
    void desactivate_shouldPropagateException_whenUsecaseFails() {
        when(desactivateUsecase.execute(99L)).thenThrow(new MetierException("ERROR"));

        var controller = controller();

        assertThatThrownBy(() -> controller.desactivate(99L))
            .isInstanceOf(MetierException.class);
    }
}