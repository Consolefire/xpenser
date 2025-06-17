package com.consolefire.xpenser.store.api.controller;

import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.store.service.ReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
@Slf4j
public class ReceiptApiController {

    private final ReceiptService receiptService;

    /**
     * Handles the POST request to create a new receipt.
     *
     * @param requestDto The ReceiptRequestDto received in the request body.
     * @return A Mono that emits the created ReceiptResponseDto, or an error.
     */
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status code to 201 Created
    public Mono<ReceiptResponseDto> createReceipt(@Valid @RequestBody Mono<ReceiptRequestDto> requestDto) {
        log.info("Received request to create a new receipt.");
        return requestDto
            .flatMap(receiptService::createReceipt) // Call the service to create the receipt
            .doOnSuccess(
                dto -> log.info("Successfully created receipt with identifier: {} and ID: {}", dto.getIdentifier(),
                    dto.getReceiptId()))
            .doOnError(e -> log.error("Error creating receipt: {}", e.getMessage()));
    }
}