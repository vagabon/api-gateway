package com.base.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseDomain;
import com.base.infrastructure.mapper.BaseMapper;
import com.base.infrastructure.persistence.entity.BaseEntity;
import com.base.infrastructure.persistence.repository.IBaseRepository;

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

    // Classes minimalistes pour tester l'adapter abstrait
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
        // @PersistenceContext n'est pas injecté hors Spring : on le positionne à la
        // main
        ReflectionTestUtils.setField(adapter, "em", em);
    }

    private TestEntity entity(Long id) {
        TestEntity e = new TestEntity();
        e.setId(id);
        return e;
    }

    private TestDomain domain(Long id) {
        TestDomain d = new TestDomain();
        d.setId(id);
        return d;
    }

    // ---------- create ----------

    @Test
    void create_mappeSauvegardeEtRemappe() {
        TestDomain input = domain(null);
        TestEntity entity = entity(null);
        TestDomain output = domain(1L);
        when(mapper.toEntity(input)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(output);

        TestDomain result = adapter.create(input);

        verify(repository).save(entity);
        assertThat(result).isSameAs(output);
    }

    // ---------- update ----------

    @Test
    void update_chargeLEntiteExistante_laFusionneEtLaSauvegarde() {
        TestDomain input = domain(1L);
        TestEntity incoming = entity(1L);
        TestEntity fromDb = entity(1L);
        TestEntity saved = entity(1L);
        TestDomain output = domain(1L);
        when(mapper.toEntity(input)).thenReturn(incoming);
        when(repository.findById(1L)).thenReturn(Optional.of(fromDb));
        when(repository.save(fromDb)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(output);

        TestDomain result = adapter.update(input);

        // on sauvegarde l'entité de la base (fusionnée), pas celle venant du mapper
        verify(repository).save(fromDb);
        assertThat(result).isSameAs(output);
    }

    @Test
    void update_siIntrouvable_leveEntityNotFound() {
        TestDomain input = domain(99L);
        when(mapper.toEntity(input)).thenReturn(entity(99L));
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.update(input))
            .isInstanceOf(MetierException.class)
            .hasMessage("ID not found : 99");

        verify(repository, never()).save(any());
    }

    // ---------- findById ----------

    @Test
    void findById_retourneLeDomaine() {
        TestEntity entity = entity(1L);
        TestDomain output = domain(1L);
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

    // ---------- delete ----------

    @Test
    void delete_supprimeEtRetourneLeDomaine() {
        TestEntity entity = entity(1L);
        TestDomain output = domain(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(output);

        TestDomain result = adapter.delete(1L);

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

    // ---------- findByPage ----------

    @Test
    void findByPage_construitLePageableEtMappeLeContenu() {
        TestEntity e1 = entity(1L);
        TestEntity e2 = entity(2L);
        TestDomain d1 = domain(1L);
        TestDomain d2 = domain(2L);
        Page<TestEntity> page = new PageImpl<>(List.of(e1, e2));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        List<TestDomain> result = adapter.findByPage(2, 10, "name");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        Pageable pageable = captor.getValue();
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

    // ---------- findBy ----------

    @SuppressWarnings("unchecked")
    private TypedQuery<TestEntity> mockQuery(String hql) {
        // RETURNS_SELF : les setters chaînés retournent la query elle-même
        TypedQuery<TestEntity> query = mock(TypedQuery.class, RETURNS_SELF);
        when(em.createQuery(hql, TestEntity.class)).thenReturn(query);
        return query;
    }

    @Test
    void findBy_bindeLesParametresEtPagine() {
        String hql = "from TestEntity where name = ?1 and active = ?2";
        TypedQuery<TestEntity> query = mockQuery(hql);
        TestEntity entity = entity(1L);
        TestDomain output = domain(1L);
        when(query.getResultList()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(output);

        List<TestDomain> result = adapter.findBy(hql, List.of("john", true), 5, 20);

        verify(query).setParameter(1, "john");
        verify(query).setParameter(2, true);
        verify(query).setFirstResult(5);
        verify(query).setMaxResults(20);
        assertThat(result).containsExactly(output);
    }

    @Test
    void findBy_sansValeurs_neBindeRien() {
        String hql = "from TestEntity";
        TypedQuery<TestEntity> query = mockQuery(hql);
        when(query.getResultList()).thenReturn(List.of());

        List<TestDomain> result = adapter.findBy(hql, List.of(), 0, 10);

        verify(query, never()).setParameter(eq(1), any());
        assertThat(result).isEmpty();
    }
}