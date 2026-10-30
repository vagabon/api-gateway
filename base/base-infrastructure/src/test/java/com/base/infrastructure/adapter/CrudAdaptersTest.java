package com.base.infrastructure.adapter;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseCrudDomain;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;
import com.base.infrastructure.jpa.repository.BaseJpaRepository;
import com.base.infrastructure.mapper.BaseMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrudAdaptersTest {

    private class User extends BaseCrudDomain {

    }

    private class UserJpaEntity extends BaseCrudJpaEntity {
    }

    private interface UserMapper extends BaseMapper<UserJpaEntity, User> {
    }

    @Mock
    BaseJpaRepository<UserJpaEntity> repository;
    @Mock
    UserMapper mapper;
    @Mock
    EntityManager em;

    private TestAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TestAdapter();
    }

    class TestAdapter extends BaseCrudAdapter<UserJpaEntity, User>
        implements FindByPageAdapter<UserJpaEntity, User>,
        DeleteAdapter<UserJpaEntity, User> {

        @Override
        public BaseJpaRepository<UserJpaEntity> getRepository() {
            return repository;
        }

        @Override
        public UserMapper getMapper() {
            return mapper;
        }

        @Override
        public EntityManager getEm() {
            return em;
        }

        @Override
        public Class<UserJpaEntity> getEntityClass() {
            return UserJpaEntity.class;
        }
    }

    // ---------- create ----------

    @Test
    void create_shouldMapSaveAndReturnDomain() {
        var domain = new User();
        var entity = new UserJpaEntity();
        var expected = new User();
        when(mapper.toJpaEntity(domain)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(expected);

        var result = adapter.create(domain);

        assertThat(result).isSameAs(expected);
        verify(repository).save(entity);
    }

    // ---------- findById ----------

    @Test
    void findById_shouldReturnMappedDomain_whenFound() {
        var entity = new UserJpaEntity();
        var expected = new User();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        assertThat(adapter.findById(1L)).isSameAs(expected);
    }

    @Test
    void findById_shouldThrowMetierException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.findById(99L))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining("ID not found : 99");
    }

    // ---------- delete ----------

    @Test
    void delete_shouldDeleteEntityAndReturnTrue_whenFound() {
        var entity = new UserJpaEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(adapter.delete(1L)).isTrue();
        verify(repository).delete(entity);
    }

    @Test
    void delete_shouldThrowAndNotDelete_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.delete(99L))
            .isInstanceOf(MetierException.class);
        verify(repository, never()).delete(any());
    }

    // ---------- desactivate ----------

    @Test
    void desactivate_shouldSetInactiveAndDeletedDate_thenSave() {
        var entity = new UserJpaEntity();
        entity.setActive(true);
        var expected = new User();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);
        var before = Instant.now();

        var result = adapter.desactivate(1L);

        assertThat(result).isSameAs(expected);
        assertThat(entity.getActive()).isFalse();
        assertThat(entity.getDeletedDate()).isBetween(before, Instant.now());
        verify(repository).save(entity);
    }

    @Test
    void desactivate_shouldThrowAndNotSave_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.desactivate(99L))
            .isInstanceOf(MetierException.class);
        verify(repository, never()).save(any());
    }

    // ---------- update ----------

    @Test
    void update_shouldThrowAndNotSave_whenIdUnknown() {
        var domain = new User();
        domain.setId(99L);
        when(mapper.toJpaEntity(domain)).thenReturn(new UserJpaEntity());
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.update(domain))
            .isInstanceOf(MetierException.class)
            .hasMessageContaining("99");
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldSaveEntityFromDatabaseAndReturnMappedResult() {
        var domain = new User();
        domain.setId(1L);
        var incoming = new UserJpaEntity();
        var fromDb = new UserJpaEntity();
        var expected = new User();
        when(mapper.toJpaEntity(domain)).thenReturn(incoming);
        when(repository.findById(1L)).thenReturn(Optional.of(fromDb));
        when(repository.save(fromDb)).thenReturn(fromDb);
        when(mapper.toDomain(fromDb)).thenReturn(expected);

        var result = adapter.update(domain);

        assertThat(result).isSameAs(expected);
        verify(repository).save(fromDb);
    }

    // ---------- findByPage ----------

    @Test
    void findByPage_shouldBuildPageRequestWithAscSort_andMapContent() {
        var entity = new UserJpaEntity();
        var mapped = new User();
        when(repository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(java.util.List.of(entity)));
        when(mapper.toDomain(entity)).thenReturn(mapped);

        var result = adapter.findByPage(2, 10, "username");

        var captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        assertThat(captor.getValue())
            .isEqualTo(PageRequest.of(2, 10,
                Sort.by(Sort.Direction.ASC, "username")));
        assertThat(result.content()).containsExactly(mapped);
    }
}