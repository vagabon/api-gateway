package com.base.infrastructure.adapter;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;

import com.base.domain.model.BaseCrudDomain;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;
import com.base.infrastructure.mapper.BaseMapper;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.Getter;
import lombok.Setter;

@ExtendWith(MockitoExtension.class)
class FindByAdapterTest {

    @Getter
    @Setter
    private class User extends BaseCrudDomain {
        String username;
        String email;
    }

    @Getter
    @Setter
    private class UserJpaEntity extends BaseCrudJpaEntity {
        String username;
        String email;
    }

    private interface UserMapper extends BaseMapper<UserJpaEntity, User> {
    }

    @Mock
    EntityManager em;

    @Mock
    UserMapper mapper;

    @Mock
    BaseJpaRepository<UserJpaEntity> repository;

    private TestAdapter adapter;

    class TestAdapter implements FindByAdapter<UserJpaEntity, User> {
        @Override
        public Class<UserJpaEntity> getEntityClass() {
            return UserJpaEntity.class;
        }

        @Override
        public EntityManager getEm() {
            return em;
        }

        @Override
        public BaseJpaRepository<UserJpaEntity> getRepository() {
            return repository;
        }

        @Override
        public UserMapper getMapper() {
            return mapper;
        }
    }

    @BeforeEach
    void setUp() {
        adapter = new TestAdapter();
    }

    private List<UserJpaEntity> createUser(int nb) {
        var users = new ArrayList<UserJpaEntity>();
        for (int i = 1; i <= nb; i++) {
            var user = new UserJpaEntity();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setActive(i % 2 == 0);
            users.add(user);
        }
        return users;
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBy_shouldBeVulnerableToHqlInjectionThroughSql_documentsRisk() {
        var sql = "from UserJpaEntity where username = 'x' or 1=1";

        var session = mock(Session.class);
        var query = mock(SelectionQuery.class);
        when(em.unwrap(Session.class)).thenReturn(session);
        when(session.createSelectionQuery(sql, UserJpaEntity.class))
            .thenReturn(query);
        when(query.setPage(any())).thenReturn(query);
        when(query.getResultList()).thenReturn(createUser(25));

        var result = adapter.findBy(sql, List.of(), 1, 100);

        assertThat(result.content()).hasSize(25);
    }
}