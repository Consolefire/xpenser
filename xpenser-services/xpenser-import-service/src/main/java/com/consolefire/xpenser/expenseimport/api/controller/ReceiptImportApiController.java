package com.consolefire.xpenser.expenseimport.api.controller;

import com.consolefire.xpenser.expenseimport.data.dto.ReceiptImportDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/import/receipts")
public class ReceiptImportApiController {


    @PostMapping
    public ResponseEntity<String> addReceipt(
        @Valid @RequestBody ReceiptImportDto receiptImportDto) {
        String responseMessage = String.format("Receipt '%s' accepted for import.", receiptImportDto.getIdentifier());
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

}
