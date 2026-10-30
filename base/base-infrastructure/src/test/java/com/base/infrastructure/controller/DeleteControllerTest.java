package com.base.infrastructure.controller;

import com.base.application.DeleteUsecase;
import com.base.domain.exception.MetierException;

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
class DeleteControllerTest {

    @Mock
    DeleteUsecase deleteUsecase;

    private class ProfileDeleteController implements DeleteController {

        @Override
        public DeleteUsecase getDeleteUsecase() {
            return deleteUsecase;
        }
    }

    private DeleteController controller() {
        return new ProfileDeleteController();
    }

    @Test
    void delete_shouldReturn200WithTrue_whenUsecaseDeletes() {
        when(deleteUsecase.execute(1L)).thenReturn(true);

        var response = controller().delete(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
        verify(deleteUsecase).execute(1L);
    }

    @Test
    void delete_shouldReturn200WithFalse_whenNothingDeleted() {
        when(deleteUsecase.execute(99L)).thenReturn(false);

        var response = controller().delete(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }

    @Test
    void delete_shouldPropagateException_whenUsecaseFails() {
        when(deleteUsecase.execute(99L)).thenThrow(new MetierException("ERROR"));

        var controller = controller();

        assertThatThrownBy(() -> controller.delete(99L))
            .isInstanceOf(MetierException.class);
    }
}