package com.base.domain.port.in;

import java.util.List;

public interface IBaseUsecase<R> {

    String ping();

    R create(R domain);

    default void doBeforeCreate(R domain) {
    }

    default void doAfterCreate(R domain) {
    }

    R update(R domain);

    default void doBeforeUpdate(R domain) {
    }

    default void doAfterUpdate(R domain) {
    }

    boolean delete(Long id);

    R desactivate(Long id);

    default void doBeforeDelete(R domain) {
    }

    default void doAfterDelete(R domain) {
    }

    List<R> findAll(Integer page, Integer max, String sort);

    R findById(Long id);

    List<R> findBy(String fields, List<Object> fieldsValues, Integer first, Integer max);
}
