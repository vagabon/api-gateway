package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseCrudDomain;
import com.base.domain.port.FindByIdRepositoryPort;
import com.base.domain.port.UpdateRepositoryPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesactivateUsecaseTest {

    private class User extends BaseCrudDomain {
    }

    @Mock
    FindByIdRepositoryPort<User> findByIdRepositoryPort;
    @Mock
    UpdateRepositoryPort<User> updateRepositoryPort;

    @InjectMocks
    DesactivateUsecase<User> usecase;

    @Test
    void execute_shouldReturnWhatRepositoryDesactivates() {
        var desactivated = new User();
        desactivated.setId(1L);
        when(findByIdRepositoryPort.findById(1L)).thenReturn(desactivated);
        when(updateRepositoryPort.update(desactivated)).thenReturn(desactivated);

        var result = usecase.execute(1L);

        verify(updateRepositoryPort).update(desactivated);
        assertThat(result.getActive()).isFalse();
        assertThat(result.getDeletedDate()).isNotNull();
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        assertThatThrownBy(() -> usecase.execute(null))
            .isInstanceOf(MetierException.class);
    }
}