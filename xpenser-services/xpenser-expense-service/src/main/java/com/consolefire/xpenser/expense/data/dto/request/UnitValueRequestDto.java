package com.consolefire.xpenser.expense.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder
public final class UnitValueRequestDto {

    @JsonProperty("unitCode")
    private final String unitCode;

    @JsonProperty("value")
    private final BigDecimal value;
}