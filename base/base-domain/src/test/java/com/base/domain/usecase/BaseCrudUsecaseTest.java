package com.base.domain.usecase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.base.domain.exception.MetierException;
import com.base.domain.model.BaseDomain;
import com.base.domain.model.PageResult;
import com.base.domain.port.out.IBaseRepositoryPortOut;
import com.base.domain.user.CurrentUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseCrudUsecaseTest {

    static class TestDomain extends BaseDomain {
    }

    static class TestUseCase
        extends BaseCrudUsecase<TestDomain, IBaseRepositoryPortOut<TestDomain>> {
        final List<String> calls = new ArrayList<>();

        TestUseCase(IBaseRepositoryPortOut<TestDomain> port) {
            super(port);
        }

        @Override
        public void doBeforeCreate(CurrentUser user, TestDomain d) {
            calls.add("beforeCreate");
        }

        @Override
        public void doAfterCreate(CurrentUser user, TestDomain d) {
            calls.add("afterCreate");
        }

        @Override
        public void doBeforeUpdate(CurrentUser user, TestDomain d) {
            calls.add("beforeUpdate");
        }

        @Override
        public void doAfterUpdate(CurrentUser user, TestDomain d) {
            calls.add("afterUpdate");
        }

        @Override
        public void doBeforeDelete(CurrentUser user, TestDomain d) {
            calls.add("beforeDelete");
        }

        @Override
        public void doAfterDelete(CurrentUser user, TestDomain d) {
            calls.add("afterDelete");
        }
    }

    @Mock
    private IBaseRepositoryPortOut<TestDomain> port;

    private TestUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TestUseCase(port);
    }

    private TestDomain domain(Long id) {
        var d = new TestDomain();
        d.setId(id);
        d.setActive(true);
        return d;
    }

    @Test
    void create_appelleHooksEtPort_dansLOrdre() {
        var user = new CurrentUser(1L, "", "", null);
        var input = domain(null);
        var saved = domain(1L);
        when(port.create(input)).thenReturn(saved);

        var result = useCase.create(user, input);

        assertThat(result).isSameAs(saved);
        assertThat(useCase.calls).containsExactly("beforeCreate", "afterCreate");
        verify(port).create(input);
    }

    @Test
    void update_appelleHooksEtPort() {
        var user = new CurrentUser(1L, "", "", null);
        var input = domain(1L);
        var updated = domain(1L);
        when(port.update(input)).thenReturn(updated);

        var result = useCase.update(user, input);

        assertThat(result).isSameAs(updated);
        assertThat(useCase.calls).containsExactly("beforeUpdate", "afterUpdate");
    }

    @Test
    void update_siLePortLeveUneException_neDeclenchePasAfterUpdate() {
        var user = new CurrentUser(1L, "", "", null);
        var input = domain(1L);
        when(port.update(input)).thenThrow(new IllegalStateException("boom"));

        assertThatThrownBy(() -> useCase.update(user, input))
            .isInstanceOf(IllegalStateException.class);

        assertThat(useCase.calls).containsExactly("beforeUpdate");
    }

    @Test
    void delete_chargeAvantDeSupprimer_etRetourneTrue() {
        var user = new CurrentUser(1L, "", "", null);
        var before = domain(1L);
        var deleted = domain(1L);
        when(port.findById(1L)).thenReturn(before);
        when(port.delete(1L)).thenReturn(deleted);

        var result = useCase.delete(user, 1L);

        assertThat(result).isTrue();
        assertThat(useCase.calls).containsExactly("beforeDelete", "afterDelete");
        var order = inOrder(port);
        order.verify(port).findById(1L);
        order.verify(port).delete(1L);
    }

    @Test
    void delete_siIntrouvable_neSupprimeRien() {
        var user = new CurrentUser(1L, "", "", null);
        when(port.findById(99L)).thenThrow(new MetierException("not found"));

        assertThatThrownBy(() -> useCase.delete(user, 99L))
            .isInstanceOf(RuntimeException.class);

        verify(port, never()).delete(anyLong());
        assertThat(useCase.calls).isEmpty();
    }

    @Test
    void desactivate_passeActiveAFalse_renseigneDeletedDate_sansSuppressionPhysique() {
        var user = new CurrentUser(1L, "", "", null);
        var before = domain(1L);
        when(port.findById(1L)).thenReturn(before);
        when(port.update(any(TestDomain.class))).thenAnswer(inv -> inv.getArgument(0));

        var start = Instant.now();
        var result = useCase.desactivate(user, 1L);

        var captor = ArgumentCaptor.forClass(TestDomain.class);

        verify(port).update(captor.capture());
        assertThat(captor.getValue().getActive()).isFalse();
        assertThat(captor.getValue().getDeletedDate()).isAfterOrEqualTo(start);
        assertThat(result.getActive()).isFalse();
        assertThat(useCase.calls).containsExactly("beforeDelete", "afterDelete");
        verify(port, never()).delete(anyLong());
    }

    @Test
    void findAll_deleguePagination() {
        var list = List.of(domain(1L), domain(2L));
        when(port.findByPage(0, 10, "name")).thenReturn(list);

        assertThat(useCase.findAll(0, 10, "name")).isEqualTo(list);
    }

    @Test
    void findById_delegueAuPort() {
        var found = domain(1L);
        when(port.findById(1L)).thenReturn(found);

        assertThat(useCase.findById(1L)).isSameAs(found);
    }

    @Test
    void findBy_delegueAuPort() {
        List<Object> values = List.of("john");
        var list = List.of(domain(1L));
        var pageResult = new PageResult<TestDomain>(0, 0, null, null, list);
        when(port.findBy("from X where name = ?1", values, 0, 10)).thenReturn(pageResult);

        assertThat(useCase.findBy("from X where name = ?1", values, 0, 10))
            .isEqualTo(pageResult);
    }
}