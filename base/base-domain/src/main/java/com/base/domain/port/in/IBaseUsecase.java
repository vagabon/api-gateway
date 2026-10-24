package com.base.domain.port.in;

import java.util.List;

import com.base.domain.model.PageResult;
import com.base.domain.user.CurrentUser;

public interface IBaseUsecase<D> {

    String ping();

    D create(CurrentUser userConnected, D domain);

    default void doBeforeCreate(CurrentUser userConnected, D domain) {
    }

    default void doAfterCreate(CurrentUser userConnected, D domain) {
    }

    D update(CurrentUser userConnected, D domain);

    default void doBeforeUpdate(CurrentUser userConnected, D domain) {
    }

    default void doAfterUpdate(CurrentUser userConnected, D domain) {
    }

    boolean delete(CurrentUser userConnected, Long id);

    D desactivate(CurrentUser userConnected, Long id);

    default void doBeforeDelete(CurrentUser userConnected, D domain) {
    }

    default void doAfterDelete(CurrentUser userConnected, D domain) {
    }

    List<D> findAll(Integer page, Integer max, String sort);

    D findById(Long id);

    PageResult<D> findBy(
        String fields,
        List<Object> fieldsValues,
        Integer first,
        Integer max);
}
