package com.base.application;

import java.util.List;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseIdDomain;
import com.base.domain.model.pageresult.PageResult;
import com.base.domain.port.FindByRepositoryPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindByUsecaseTest {

    private class User extends BaseIdDomain {
    }

    @Mock
    FindByRepositoryPort<User> findByRepositoryPort;
    @InjectMocks
    FindByUsecase<User> usecase;

    @Test
    void execute_shouldForwardAllParametersAndReturnRepositoryResult() {
        @SuppressWarnings("unchecked")
        PageResult<User> expected = mock(PageResult.class);
        var values = List.<Object>of("gonz", 18);
        when(findByRepositoryPort.findBy("name = ?1 and age > ?2", values, 0, 20))
            .thenReturn(expected);

        var result = usecase.execute("name = ?1 and age > ?2", values, 0, 20);

        assertThat(result).isSameAs(expected);
        verify(findByRepositoryPort).findBy("name = ?1 and age > ?2", values, 0, 20);
    }

    @Test
    void execute_shouldForwardNullParameters_withoutDefaulting() {
        when(findByRepositoryPort.findBy(null, null, null, null)).thenReturn(null);

        assertThat(usecase.execute(null, null, null, null)).isNull();
        verify(findByRepositoryPort).findBy(null, null, null, null);
    }

    @Test
    void execute_shouldPropagateException_whenRepositoryFails() {
        var values = List.<Object>of();
        when(findByRepositoryPort.findBy("bad", values, 0, 20))
            .thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> usecase.execute("bad", values, 0, 20))
            .isInstanceOf(MetierException.class);
    }
}