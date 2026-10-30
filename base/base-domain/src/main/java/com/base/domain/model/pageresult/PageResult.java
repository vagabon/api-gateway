package com.base.domain.model.pageresult;

import java.util.List;

public record PageResult<D>(
    Integer page,
    int totalPages,
    Long totalElements,
    Integer max,
    List<D> content) {
}
