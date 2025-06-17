package com.consolefire.xpenser.store.service;

import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto; // Import the new DTO
import reactor.core.publisher.Mono;

public interface ReceiptService {

    /**
     * Creates a new receipt.
     *
     * @param receiptRequestDto The DTO containing receipt data.
     * @return A Mono that emits the created ReceiptResponseDto, or an error.
     */
    Mono<ReceiptResponseDto> createReceipt(ReceiptRequestDto receiptRequestDto);
}