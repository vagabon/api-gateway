package com.base.domain.model.pageresult;

import java.util.List;

import com.base.domain.model.BaseCrudDomain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultTest {

    private class User extends BaseCrudDomain {
    }

    @Test
    void givenPageResult_whenInit_thenValueAreCorrect() {
        var users = List.of(new User());
        var pageResult = new PageResult<>(1, 1, 1L, 1, users);

        assertThat(pageResult.page()).isEqualTo(1);
        assertThat(pageResult.totalPages()).isEqualTo(1);
        assertThat(pageResult.totalElements()).isEqualTo(1L);
        assertThat(pageResult.max()).isEqualTo(1);
        assertThat(pageResult.content()).isEqualTo(users);
    }
}
