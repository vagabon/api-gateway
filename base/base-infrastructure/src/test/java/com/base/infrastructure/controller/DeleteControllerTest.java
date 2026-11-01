package com.base.infrastructure.controller;

import com.base.application.DeleteUsecase;
import com.base.domain.model.BaseIdDomain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteControllerTest {

    private class Role extends BaseIdDomain {
    }

    @Mock
    DeleteUsecase<Role> deleteUsecase;

    private class RoleDeleteController implements DeleteController<Role> {

        @Override
        public DeleteUsecase<Role> getDeleteUsecase() {
            return deleteUsecase;
        }
    }

    private DeleteController<Role> controller() {
        return new RoleDeleteController();
    }

    @Test
    void delete_shouldReturn200WithTrue_whenUsecaseDeletes() {

        var response = controller().delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(deleteUsecase).execute(1L);
    }
}