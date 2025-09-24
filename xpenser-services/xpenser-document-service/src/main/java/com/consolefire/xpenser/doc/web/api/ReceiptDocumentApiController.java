package com.consolefire.xpenser.doc.web.api;

import com.consolefire.xpenser.doc.data.dto.ReceiptDocumentDto;
import com.consolefire.xpenser.doc.data.dto.ReceiptDocumentUploadDto;
import com.consolefire.xpenser.doc.service.ReceiptDocumentService;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipts/{receiptId}/documents")
public class ReceiptDocumentApiController {

    private final ReceiptDocumentService receiptDocumentService;

    @GetMapping
    public ResponseEntity<ReceiptDocumentDto> getAllReceiptDocument(
        @PathVariable(name = "receiptId") String receiptId) {
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<ReceiptDocumentDto> createReceiptDocument(
        @PathVariable(name = "receiptId") String receiptId,
        @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Calculate checksum
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = file.getBytes();
            byte[] hashBytes = digest.digest(fileBytes);
            String checksum = HexFormat.of().formatHex(hashBytes);

            // Create the upload DTO
            ReceiptDocumentUploadDto uploadDto = ReceiptDocumentUploadDto.builder()
                .receiptId(receiptId)
                .fileName(file.getOriginalFilename())
                .fileContent(fileBytes)
                .fileChecksum(checksum)
                .build();

            // Call the service layer
            ReceiptDocumentDto resultDto = receiptDocumentService.createReceiptDocument(uploadDto);

            return new ResponseEntity<>(resultDto, HttpStatus.CREATED);

        } catch (IOException | NoSuchAlgorithmException e) {
            // Handle exceptions during file processing
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            // Handle business logic exceptions, like duplicate receipt ID
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
