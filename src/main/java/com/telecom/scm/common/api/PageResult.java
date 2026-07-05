package com.telecom.scm.common.api;

import java.util.List;

public record PageResult<T>(List<T> list, long total, int page, int pageSize) {

    public static <T> PageResult<T> empty(int page, int pageSize) {
        return new PageResult<>(List.of(), 0L, page, pageSize);
    }

    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }

    public int getTotalPages() {
        return pageSize <= 0 ? 0 : (int) Math.ceil((double) total / pageSize);
    }

    public boolean hasNext() {
        return page < getTotalPages();
    }

    public boolean hasPrevious() {
        return page > 1;
    }
}
