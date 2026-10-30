package com.base.infrastructure.controller;

import com.base.application.FindByIdUsecase;
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
class FindByIdControllerTest {

    @Mock
    FindByIdUsecase<Profile> findByIdUsecase;

    private class Profile extends BaseCrudDomain {
    }

    private class ProfileFindByIdController implements FindByIdController<Profile> {

        @Override
        public FindByIdUsecase<Profile> getFindByIdUsecase() {
            return findByIdUsecase;
        }
    }

    private FindByIdController<Profile> controller() {
        return new ProfileFindByIdController();
    }

    @Test
    void findById_shouldReturn200WithFoundEntity() {
        var found = new Profile();
        when(findByIdUsecase.execute(1L)).thenReturn(found);

        var response = controller().findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(found);
        verify(findByIdUsecase).execute(1L);
    }

    @Test
    void findById_shouldReturn200WithNullBody_whenUsecaseReturnsNull() {
        when(findByIdUsecase.execute(99L)).thenReturn(null);

        var response = controller().findById(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void findById_shouldPropagateException_whenUsecaseFails() {
        when(findByIdUsecase.execute(99L)).thenThrow(new MetierException("ERROR"));

        var controller = controller();

        assertThatThrownBy(() -> controller.findById(99L))
            .isInstanceOf(MetierException.class);
    }
}