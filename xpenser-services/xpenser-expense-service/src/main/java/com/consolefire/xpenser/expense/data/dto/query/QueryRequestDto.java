package com.consolefire.xpenser.expense.data.dto.query;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@ToString
@Builder(toBuilder = true)
public class QueryRequestDto<Q extends AbstractQueryProperties> {

    public static final QueryRequestDto DEFAULT = QueryRequestDto.builder().usePagination(false).build();

    private final Q queryProperties;
    private final boolean usePagination;
    private final PaginationRequestDto pagination;


}
