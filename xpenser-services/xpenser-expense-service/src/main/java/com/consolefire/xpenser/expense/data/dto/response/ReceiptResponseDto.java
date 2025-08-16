package com.consolefire.xpenser.expense.data.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@Builder
public final class ReceiptResponseDto {

    @JsonProperty("receiptId")
    private final Long receiptId;

    @JsonProperty("identifier")
    private final String identifier;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX", timezone = "UTC")
    private final Instant createdAt; // Using Instant for creation timestamp
}
