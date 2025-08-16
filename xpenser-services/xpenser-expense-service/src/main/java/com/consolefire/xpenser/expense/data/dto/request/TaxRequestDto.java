package com.consolefire.xpenser.expense.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder
public final class TaxRequestDto {

    @JsonProperty("total")
    private final BigDecimal total;

    @JsonProperty("taxes")
    private final List<TaxDetailRequestDto> taxes;

    // Custom constructor for builder to ensure list immutability
    // Lombok's @Builder will use this if present and matches the fields.
    // If not present, Lombok generates its own, which for final List fields
    // will typically assign the list directly. Adding this for explicit defensive copy.
    TaxRequestDto(BigDecimal total, List<TaxDetailRequestDto> taxes) {
        this.total = total;
        this.taxes = taxes != null ? Collections.unmodifiableList(taxes) : Collections.emptyList();
    }

    // Builder method to ensure defensive copy when building
    public static class TaxRequestDtoBuilder {
        public TaxRequestDtoBuilder taxes(List<TaxDetailRequestDto> taxes) {
            this.taxes = taxes != null ? Collections.unmodifiableList(taxes) : Collections.emptyList();
            return this;
        }
    }
}