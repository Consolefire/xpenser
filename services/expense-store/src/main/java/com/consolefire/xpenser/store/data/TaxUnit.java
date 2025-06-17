package com.consolefire.xpenser.store.data;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@RequiredArgsConstructor
public class TaxUnit {

    private final BigDecimal value;

    private final String unit;
}