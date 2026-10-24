package com.base.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseDomain;
import com.base.infrastructure.mapper.BaseMapper;
import com.base.infrastructure.persistence.entity.BaseEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

import org.hibernate.Session;
import org.hibernate.query.SelectionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseAdapterTest {

    static class TestEntity extends BaseEntity {
    }

    static class TestDomain extends BaseDomain {
    }

    static class TestAdapter
        extends BaseAdapter<TestEntity, TestDomain, IBaseRepository<TestEntity>> {
        TestAdapter(
            IBaseRepository<TestEntity> repository,
            BaseMapper<TestEntity, TestDomain> mapper) {
            super(repository, mapper, TestEntity.class);
        }
    }

    @Mock
    private IBaseRepository<TestEntity> repository;

    @Mock
    private BaseMapper<TestEntity, TestDomain> mapper;

    @Mock
    private EntityManager em;

    private TestAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TestAdapter(repository, mapper);
        ReflectionTestUtils.setField(adapter, "em", em);
    }

    private TestEntity entity(Long id) {
        var e = new TestEntity();
        e.setId(id);
        return e;
    }

    private TestDomain domain(Long id) {
        var d = new TestDomain();
        d.setId(id);
        return d;
    }

    @Test
    void create_mappeSauvegardeEtRemappe() {
        var input = domain(null);
        var entity = entity(null);
        var output = domain(1L);
        when(mapper.toEntity(input)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(output);

        var result = adapter.create(input);

        verify(repository).save(entity);
        assertThat(result).isSameAs(output);
    }

    @Test
    void update_chargeLEntiteExistante_laFusionneEtLaSauvegarde() {
        var input = domain(1L);
        var incoming = entity(1L);
        var fromDb = entity(1L);
        var saved = entity(1L);
        var output = domain(1L);
        when(mapper.toEntity(input)).thenReturn(incoming);
        when(repository.findById(1L)).thenReturn(Optional.of(fromDb));
        when(repository.save(fromDb)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(output);

        var result = adapter.update(input);

        verify(repository).save(fromDb);
        assertThat(result).isSameAs(output);
    }

    @Test
    void update_siIntrouvable_leveEntityNotFound() {
        var input = domain(99L);
        when(mapper.toEntity(input)).thenReturn(entity(99L));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.update(input))
            .isInstanceOf(MetierException.class)
            .hasMessage("ID not found : 99");

        verify(repository, never()).save(any());
    }

    @Test
    void findById_retourneLeDomaine() {
        var entity = entity(1L);
        var output = domain(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(output);

        assertThat(adapter.findById(1L)).isSameAs(output);
    }

    @Test
    void findById_siIntrouvable_leveEntityNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.findById(99L))
            .isInstanceOf(MetierException.class)
            .hasMessage("ID not found : 99");
    }

    @Test
    void delete_supprimeEtRetourneLeDomaine() {
        var entity = entity(1L);
        var output = domain(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(output);

        var result = adapter.delete(1L);

        verify(repository).delete(entity);
        assertThat(result).isSameAs(output);
    }

    @Test
    void delete_siIntrouvable_neSupprimeRien() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.delete(99L))
            .isInstanceOf(MetierException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    void findByPage_construitLePageableEtMappeLeContenu() {
        var e1 = entity(1L);
        var e2 = entity(2L);
        var d1 = domain(1L);
        var d2 = domain(2L);
        var page = new PageImpl<>(List.of(e1, e2));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        var result = adapter.findByPage(2, 10, "name");

        var captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        var pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("name").getDirection())
            .isEqualTo(Sort.Direction.ASC);
        assertThat(result).containsExactly(d1, d2);
    }

    @Test
    void findByPage_pageVide_retourneListeVide() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        assertThat(adapter.findByPage(0, 10, "name")).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private SelectionQuery<TestEntity> mockQuery(String hql) {
        var query = mock(SelectionQuery.class, RETURNS_SELF);
        var session = mock(Session.class);

        when(em.unwrap(Session.class)).thenReturn(session);
        when(session.createSelectionQuery(hql, TestEntity.class)).thenReturn(query);
        return query;
    }

    @Test
    void findBy_bindeLesParametresEtPagine() {
        var hql = "from TestEntity where name = ?1 and active = ?2";
        var query = mockQuery(hql);
        var entity = entity(1L);
        var output = domain(1L);
        when(query.getResultList()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(output);

        var result = adapter.findBy(hql, List.of("john", true), 5, 20);

        verify(query).setParameter(1, "john");
        verify(query).setParameter(2, true);
        assertThat(result.content()).containsExactly(output);
    }

    @Test
    void findBy_sansValeurs_neBindeRien() {
        var hql = "from TestEntity";
        var query = mockQuery(hql);
        when(query.getResultList()).thenReturn(List.of());

        var result = adapter.findBy(hql, List.of(), 0, 10);

        verify(query, never()).setParameter(eq(1), any());
        assertThat(result.content()).isEmpty();
    }
}