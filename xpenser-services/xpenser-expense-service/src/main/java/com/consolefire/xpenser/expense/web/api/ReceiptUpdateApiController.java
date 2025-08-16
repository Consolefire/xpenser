package com.consolefire.xpenser.expense.web.api;

import com.consolefire.xpenser.expense.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.expense.data.dto.request.ReceiptUpdateRequestDto;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.expense.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/receipts")
public class ReceiptUpdateApiController {

    private final ReceiptService receiptService;

    // POST: Create new Receipt
    @PostMapping
    public ResponseEntity<ReceiptResponseDto> createReceipt(@RequestBody ReceiptRequestDto receiptRequestDto) {
        log.info("Creating a new receipt for identifier: {}, marketplace: {}, store: {}",
            receiptRequestDto.getIdentifier(), receiptRequestDto.getMarketPlaceCode(),
            receiptRequestDto.getStoreCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(receiptService.createReceipt(receiptRequestDto));
    }

    // PUT: Update Receipt
    @PutMapping("/{marketPlaceCode}/{storeCode}/{identifier}")
    public ResponseEntity<ReceiptResponseDto> updateReceipt(
        @PathVariable(name = "marketPlaceCode") final String marketPlaceCode,
        @PathVariable(name = "storeCode") final String storeCode,
        @PathVariable(name = "identifier") final String identifier,
        @RequestBody ReceiptUpdateRequestDto updateRequestDto) {
        log.info("Updating receipt");
        ReceiptRequestDto receiptRequestDto = ReceiptRequestDto.builder()
            .marketPlaceCode(marketPlaceCode)
            .storeCode(storeCode)
            .identifier(identifier)
            .metadata(updateRequestDto.getMetadata())
            .total(updateRequestDto.getTotal())
            .currency(updateRequestDto.getCurrency())
            .payment(updateRequestDto.getPayment())
            .items(updateRequestDto.getItems())
            .build();
        return ResponseEntity.accepted().body(receiptService.updateReceipt(receiptRequestDto));
    }

}
