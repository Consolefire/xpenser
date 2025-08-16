package com.consolefire.xpenser.expense.data.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@ToString
@Builder(toBuilder = true)
public class PagedResponseDto<T> {

    private final int maxPageSize;
    private final int currentPageSize;
    private final int currentPage;
    private final int totalPages;
    @ToString.Exclude
    private final List<T> result;

}
