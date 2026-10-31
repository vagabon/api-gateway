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
    UpdateUsecase<Role> updateUsecase;

    private class Role extends BaseCrudDomain {
    }

    private class RoleUpdateController implements UpdateController<Role> {

        @Override
        public UpdateUsecase<Role> getUpdateUsecase() {
            return updateUsecase;
        }
    }

    private UpdateController<Role> controller() {
        return new RoleUpdateController();
    }

    @Test
    void update_shouldReturn200WithUpdatedEntity() {
        var request = new Role();
        var updated = new Role();
        when(updateUsecase.execute(request)).thenReturn(updated);

        var response = controller().update(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(updated);
        verify(updateUsecase).execute(request);
    }

    @Test
    void update_shouldPropagateException_whenUsecaseFails() {
        var request = new Role();
        when(updateUsecase.execute(request)).thenThrow(new MetierException("ERROR"));

        var controller = controller();

        assertThatThrownBy(() -> controller.update(request))
            .isInstanceOf(MetierException.class);
    }
}