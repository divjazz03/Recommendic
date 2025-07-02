package com.divjazz.recommendic.global.general;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record PageResponse<T>(
        List<T> content,
        int totalPages,
        long totalElements,
        boolean last,
        int size,
        int number,
        Sort sort,
        int numberOfElements,
        boolean first,
        boolean empty

) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.getSize(),
                page.getNumber(),
                new Sort(page.getSort().isEmpty(), page.getSort().isSorted()),
                page.getNumberOfElements(),
                page.isFirst(),
                page.isEmpty()
        );
    }
    public static <T> PageResponse<T> fromSet(Pageable pageable, Set<T> result, long total) {
        if (Objects.isNull(result) || result.isEmpty()) {
            return PageResponse.from(Page.empty());
        }
        var isLast = result.size() < pageable.getPageSize();
        var isFirst = pageable.getPageNumber() == 1;
        return new PageResponse<>(
                result.stream().toList(),
                (int) (total / pageable.getPageSize()),
                total,
                isLast,
                result.size(),
                pageable.getPageNumber(),
                new Sort(true, true),
                result.size(),
                isFirst,
                false
        );
    }
}

