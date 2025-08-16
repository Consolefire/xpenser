package com.consolefire.xpenser.expense.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Range<R> {

    @With
    private final R from;
    @With
    private final R to;

    private boolean inclusive = true;

}
