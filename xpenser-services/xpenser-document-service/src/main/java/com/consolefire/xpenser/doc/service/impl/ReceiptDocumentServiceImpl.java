package com.consolefire.xpenser.doc.service.impl;

import static java.lang.String.format;

import com.consolefire.xpenser.doc.data.dto.FileMetadataDto;
import com.consolefire.xpenser.doc.data.dto.ReceiptDocumentDto;
import com.consolefire.xpenser.doc.data.dto.ReceiptDocumentUploadDto;
import com.consolefire.xpenser.doc.data.entity.FileEntity;
import com.consolefire.xpenser.doc.data.entity.ReceiptDocumentEntity;
import com.consolefire.xpenser.doc.data.repository.FileEntityRepository;
import com.consolefire.xpenser.doc.data.repository.ReceiptDocumentRepository;
import com.consolefire.xpenser.doc.service.FileStorageService;
import com.consolefire.xpenser.doc.service.ReceiptDocumentService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReceiptDocumentServiceImpl implements ReceiptDocumentService {

    private final ReceiptDocumentRepository receiptDocumentRepository;
    private final FileStorageService fileStorageService;
    private final FileEntityRepository fileEntityRepository;


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReceiptDocumentDto createReceiptDocument(ReceiptDocumentUploadDto receiptDocumentUploadDto) {
        Optional<ReceiptDocumentEntity> existingDocument = receiptDocumentRepository.findByReceiptId(
            receiptDocumentUploadDto.getReceiptId());
        if (existingDocument.isPresent()) {
            throw new IllegalArgumentException("A document with this receipt ID already exists.");
        }

        // Check if a file with the same checksum exists
        Optional<FileEntity> existingFile =
            existingDocument.map(ReceiptDocumentEntity::getReceiptFile);
        if (existingFile.isPresent()) {
            Path path = fileStorageService.loadPath(existingFile.get().getFileName());
            if (path.toFile().exists()) {
                throw new IllegalArgumentException("A file with this receipt already exists.");
            }
        }

        final UUID fileId = UUID.randomUUID();
        final String fileName = format("%s__%s",
            fileId, receiptDocumentUploadDto.getFileName());

        // Save the file to disk
        FileMetadataDto fileMetadataDto = fileStorageService.store(
            fileName,
            receiptDocumentUploadDto.getFileContent()
        );

        // Create and save the entities

        FileEntity fileEntity = FileEntity.builder()
            .fileId(fileId)
            .fileName(fileName)
            .location(fileMetadataDto.getFileUrl())
            .checksum(receiptDocumentUploadDto.getFileChecksum())
            .build();

        ReceiptDocumentEntity receiptDocumentEntity = ReceiptDocumentEntity.builder()
            .receiptId(receiptDocumentUploadDto.getReceiptId())
            .receiptFile(fileEntity)
            .build();

        ReceiptDocumentEntity savedEntity = receiptDocumentRepository.save(receiptDocumentEntity);

        // Convert and return DTO
        return ReceiptDocumentDto.builder()
            .id(savedEntity.getId())
            .receiptId(savedEntity.getReceiptId())
            .fileIdentifier(savedEntity.getReceiptFile().getFileId().toString())
            .fileName(savedEntity.getReceiptFile().getFileName())
            .fileUrl(savedEntity.getReceiptFile().getLocation())
            .fileChecksum(savedEntity.getReceiptFile().getChecksum())
            .build();
    }

}
