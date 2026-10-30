package com.base.infrastructure.controller;

import java.util.List;

import com.base.application.FindByUsecase;
import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseCrudDomain;
import com.base.domain.model.pageresult.PageResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindByControllerTest {

    @Mock
    FindByUsecase<Profile> findByUsecase;

    private class Profile extends BaseCrudDomain {
    }

    private class ProfileFindByController implements FindByController<Profile> {
        @Override
        public FindByUsecase<Profile> getFindByUsecase() {
            return findByUsecase;
        }
    }

    private final FindByController<Profile> controller;

    FindByControllerTest() {
        controller = new ProfileFindByController();
    }

    @SuppressWarnings("unchecked")
    private PageResult<Profile> page() {
        return mock(PageResult.class);
    }

    @Test
    void findBy_shouldReturn200WithUsecaseResult() {
        var expected = page();
        when(findByUsecase.execute(anyString(), anyList(), any(), any()))
            .thenReturn(expected);

        var response = controller.findBy("name", "gonz", 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    void findBy_shouldDefaultFirstTo0AndMaxTo100_whenNull() {
        when(findByUsecase.execute(anyString(), anyList(), any(), any()))
            .thenReturn(page());

        controller.findBy("name", "gonz", null, null);

        verify(findByUsecase).execute(anyString(), anyList(),
            org.mockito.ArgumentMatchers.eq(0),
            org.mockito.ArgumentMatchers.eq(100));
    }

    @Test
    void findBy_shouldForwardFirstAndMax() {
        when(findByUsecase.execute(anyString(), anyList(), any(), any()))
            .thenReturn(page());

        controller.findBy("name", "gonz", 3, 25);

        verify(findByUsecase).execute(anyString(), anyList(),
            org.mockito.ArgumentMatchers.eq(3),
            org.mockito.ArgumentMatchers.eq(25));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBy_shouldKeepStringValueAsIs() {
        when(findByUsecase.execute(anyString(), anyList(), any(), any()))
            .thenReturn(page());

        controller.findBy("name", "gonz", 0, 10);

        var sql = ArgumentCaptor.forClass(String.class);
        var values = ArgumentCaptor.forClass(List.class);
        verify(findByUsecase).execute(sql.capture(), values.capture(), any(), any());
        assertThat(sql.getValue()).contains("name");
        assertThat(values.getValue()).containsExactly("gonz");
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBy_shouldConvertTrueAndFalseToBoolean() {
        when(findByUsecase.execute(anyString(), anyList(), any(), any()))
            .thenReturn(page());

        controller.findBy("activeAndDeleted", "true,false", 0, 10);

        var values = ArgumentCaptor.forClass(List.class);
        verify(findByUsecase).execute(anyString(), values.capture(), any(), any());
        assertThat(values.getValue()).containsExactly(Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    void findBy_shouldPropagateException_whenUsecaseFails() {
        when(findByUsecase.execute(anyString(), anyList(), any(), any()))
            .thenThrow(new MetierException("ERROR"));

        assertThatThrownBy(() -> controller.findBy("name", "gonz", 0, 10))
            .isInstanceOf(MetierException.class);
    }

    @Test
    void findBy_shouldFail_whenMoreValuesThanFields() {
        assertThatThrownBy(() -> controller.findBy("name", "a,b", 0, 10))
            .isInstanceOf(IndexOutOfBoundsException.class);
    }
}