package com.consolefire.xpenser.expense.data.dto.query;

import com.consolefire.xpenser.expense.util.Range;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

@With
@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class ReceiptQueryProperties extends AbstractQueryProperties {

    private final String identifier;
    private final String marketPlaceCode;
    private final String storeCode;
    private final Instant generatedAt;
    private final Range<Instant> generatedRange;

}
