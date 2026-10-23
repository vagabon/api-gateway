
package com.base.infrastructure.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.base.domain.model.BaseDomain;
import com.base.domain.usecase.BaseCrudUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    // Domaine et service minimalistes pour tester la classe abstraite
    static class TestDomain extends BaseDomain {
    }

    static class TestService extends BaseService<TestDomain> {
        TestService(BaseCrudUseCase<TestDomain> useCase) {
            super(useCase);
        }
    }

    @Mock
    private BaseCrudUseCase<TestDomain> useCase;

    private TestService service;

    @BeforeEach
    void setUp() {
        service = new TestService(useCase);
    }

    private TestDomain domain(Long id) {
        TestDomain d = new TestDomain();
        d.setId(id);
        return d;
    }

    // ---------- délégations simples ----------

    @Test
    void ping_delegueAuUseCase() {
        when(useCase.ping()).thenReturn("pong");

        assertThat(service.ping()).isEqualTo("pong");
    }

    @Test
    void create_delegueAuUseCase() {
        TestDomain input = domain(null);
        TestDomain created = domain(1L);
        when(useCase.create(input)).thenReturn(created);

        assertThat(service.create(input)).isSameAs(created);
    }

    @Test
    void findById_delegueAuUseCase() {
        TestDomain found = domain(1L);
        when(useCase.findById(1L)).thenReturn(found);

        assertThat(service.findById(1L)).isSameAs(found);
    }

    @Test
    void delete_delegueAuUseCase() {
        when(useCase.delete(1L)).thenReturn(true);

        assertThat(service.delete(1L)).isTrue();
        verify(useCase).delete(1L);
    }

    @Test
    void desactivate_delegueAuUseCase() {
        TestDomain off = domain(1L);
        when(useCase.desactivate(1L)).thenReturn(off);

        assertThat(service.desactivate(1L)).isSameAs(off);
    }

    @Test
    void findAll_delegueAuUseCase() {
        List<TestDomain> list = List.of(domain(1L), domain(2L));
        when(useCase.findAll(0, 10, "name")).thenReturn(list);

        assertThat(service.findAll(0, 10, "name")).isEqualTo(list);
    }

    // ---------- update ----------

    @Test
    void update_avecId_delegueAuUseCase() {
        TestDomain input = domain(1L);
        TestDomain updated = domain(1L);
        when(useCase.update(input)).thenReturn(updated);

        assertThat(service.update(input)).isSameAs(updated);
    }

    @Test
    void update_sansId_leveEntityNotFound_sansAppelerLeUseCase() {
        TestDomain input = domain(null);

        assertThatThrownBy(() -> service.update(input)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("id is null");

        verifyNoInteractions(useCase);
    }

    // ---------- findBy ----------

    @Test
    void findBy_valeursNulles_appliqueFirst0EtMax100() {
        when(useCase.findBy(anyString(), anyList(), anyInt(), anyInt())).thenReturn(List.of());

        service.findBy("name", List.of("john"), null, null);

        verify(useCase).findBy(anyString(), anyList(), org.mockito.ArgumentMatchers.eq(0),
                org.mockito.ArgumentMatchers.eq(100));
    }

    @Test
    void findBy_conserveFirstEtMaxFournis() {
        when(useCase.findBy(anyString(), anyList(), anyInt(), anyInt())).thenReturn(List.of());

        service.findBy("name", List.of("john"), 20, 50);

        verify(useCase).findBy(anyString(), anyList(), org.mockito.ArgumentMatchers.eq(20),
                org.mockito.ArgumentMatchers.eq(50));
    }

    @Test
    void findBy_construitUneRequeteNonVide_etRetourneLeResultat() {
        List<TestDomain> expected = List.of(domain(1L));
        when(useCase.findBy(anyString(), anyList(), anyInt(), anyInt())).thenReturn(expected);

        List<TestDomain> result = service.findBy("name", List.of("john"), 0, 10);

        ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
        verify(useCase).findBy(query.capture(), anyList(), anyInt(), anyInt());

        assertThat(query.getValue()).isNotBlank();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findBy_valeurQuelconque_resteInchangee() {
        when(useCase.findBy(anyString(), anyList(), anyInt(), anyInt())).thenReturn(List.of());

        service.findBy("age", List.of(42), 0, 10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);
        verify(useCase).findBy(anyString(), captor.capture(), anyInt(), anyInt());

        assertThat(captor.getValue()).containsExactly(42);
        verify(useCase, never()).create(any());
    }
}