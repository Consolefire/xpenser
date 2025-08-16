package com.consolefire.xpenser.expense.service.impl;

import com.consolefire.xpenser.expense.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptCreateResponseDto;
import com.consolefire.xpenser.expense.data.entiry.ReceiptEntity;
import com.consolefire.xpenser.expense.data.exceptions.DuplicateEntryExistsException;
import com.consolefire.xpenser.expense.data.exceptions.EntryDoesNotExistsException;
import com.consolefire.xpenser.expense.data.mapper.ReceiptMapper;
import com.consolefire.xpenser.expense.data.repository.ReceiptRepository;
import com.consolefire.xpenser.expense.service.ReceiptService;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReceiptCreateResponseDto createReceipt(ReceiptRequestDto receiptRequestDto) {
        log.info("Attempting to create receipt for identifier: {}", receiptRequestDto.getIdentifier());

        Optional<ReceiptEntity> existingReceipt = receiptRepository.findByIdentifierAndMarketPlaceCodeAndStoreCode(
            receiptRequestDto.getIdentifier(),
            receiptRequestDto.getMarketPlaceCode(),
            receiptRequestDto.getStoreCode()
        );

        if (existingReceipt.isPresent()) {
            throw new DuplicateEntryExistsException("A receipt with this identifier already exists.");
        }

        ReceiptEntity entity = receiptMapper.toEntity(receiptRequestDto);
        entity.setCreatedAt(Instant.now());

        ReceiptEntity savedEntity = receiptRepository.save(entity);

        return receiptMapper.toResponseDto(savedEntity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReceiptCreateResponseDto updateReceipt(ReceiptRequestDto receiptRequestDto) {
        log.info("Attempting to update receipt for identifier: {}", receiptRequestDto.getIdentifier());

        ReceiptEntity existingReceipt = receiptRepository.findByIdentifierAndMarketPlaceCodeAndStoreCode(
            receiptRequestDto.getIdentifier(),
            receiptRequestDto.getMarketPlaceCode(),
            receiptRequestDto.getStoreCode()
        ).orElseThrow(() -> new EntryDoesNotExistsException("No receipt found with this identifier to update."));

        receiptRequestDto = receiptRequestDto.withTimestamp(existingReceipt.getGeneratedAt());

        receiptMapper.updateEntityFromDto(receiptRequestDto, existingReceipt);

        ReceiptEntity updatedEntity = receiptRepository.save(existingReceipt);

        return receiptMapper.toResponseDto(updatedEntity);
    }
}