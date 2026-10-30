package com.base.application;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByPageRepositoryPort;

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
class FindByPageUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    FindByPageRepositoryPort<User> findByPageRepositoryPort;
    @InjectMocks
    FindByPageUsecase<User> usecase;

    @Test
    void execute_shouldReturnPageFromRepository() {
        @SuppressWarnings("unchecked")
        PageResult<User> expected = org.mockito.Mockito.mock(PageResult.class);
        when(findByPageRepositoryPort.findByPage(0, 20, "id")).thenReturn(expected);

        var result = usecase.execute(0, 20, "id");

        assertThat(result).isSameAs(expected);
        verify(findByPageRepositoryPort).findByPage(0, 20, "id");
    }

    @Test
    void execute_shouldForwardNullParameters_withoutDefaulting() {
        when(findByPageRepositoryPort.findByPage(null, null, null)).thenReturn(null);

        assertThat(usecase.execute(null, null, null)).isNull();
        verify(findByPageRepositoryPort).findByPage(null, null, null);
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        when(findByPageRepositoryPort.findByPage(0, 20, "bad"))
            .thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute(0, 20, "bad"))
            .isInstanceOf(MetierException.class);
    }
}