package com.consolefire.xpenser.store.api.controller;

import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.store.service.ReceiptService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/receipts",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ReceiptApiController {

    private final ReceiptService receiptService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status code to 201 Created
    public ResponseEntity<ReceiptResponseDto> createReceipt(@Valid @RequestBody ReceiptRequestDto requestDto) {
        log.info("Received request to create a new receipt.");
        ReceiptResponseDto responseDto = receiptService.createReceipt(requestDto);
        return ResponseEntity.of(Optional.ofNullable(responseDto));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED) // Sets the HTTP status code to 202 Accepted
    public ResponseEntity<ReceiptResponseDto> updateReceiptById(@PathVariable Long id, @RequestBody ReceiptRequestDto requestDto){
        return ResponseEntity.of(receiptService.updateReceiptById(id, requestDto));
    }
}