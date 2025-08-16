package com.consolefire.xpenser.expense.web.query;

import static java.util.Optional.ofNullable;

import com.consolefire.xpenser.expense.data.dto.query.QueryRequestDto;
import com.consolefire.xpenser.expense.data.dto.query.ReceiptQueryProperties;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.expense.service.ReceiptQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/query/receipts")
public class ReceiptQueryApiController {

    private final ReceiptQueryService receiptQueryService;

    @GetMapping("/{marketPlaceCode}/{storeCode}/{identifier}")
    public ResponseEntity<ReceiptResponseDto> updateReceipt(
        @PathVariable(name = "marketPlaceCode") final String marketPlaceCode,
        @PathVariable(name = "storeCode") final String storeCode,
        @PathVariable(name = "identifier") final String identifier) {
        ReceiptQueryProperties receiptQueryProperties = ReceiptQueryProperties.builder()
            .marketPlaceCode(marketPlaceCode)
            .storeCode(storeCode)
            .identifier(identifier)
            .build();
        QueryRequestDto<ReceiptQueryProperties> queryRequestDto = QueryRequestDto.<ReceiptQueryProperties>builder()
            .queryProperties(receiptQueryProperties)
            .build();

        return ResponseEntity.of(
            ofNullable(receiptQueryService.fetchWithIdentifier(marketPlaceCode, storeCode, identifier)).orElse(null));
    }

    // GET: All receipts >> returns List<ReceiptResponseDto>
    @GetMapping
    public ResponseEntity<List<ReceiptResponseDto>> fetchAllReceipts() {
        log.info("Fetching all receipts with default query parameters");
        return ResponseEntity.ok(receiptQueryService.fetchAllReceipts(QueryRequestDto.DEFAULT));
    }
}
