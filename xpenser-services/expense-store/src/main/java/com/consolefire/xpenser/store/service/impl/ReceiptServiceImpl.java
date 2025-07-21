package com.consolefire.xpenser.store.service.impl;

import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.store.data.entity.ReceiptEntity;
import com.consolefire.xpenser.store.data.mapper.ReceiptMapper;
import com.consolefire.xpenser.store.data.repository.ReceiptRepository;
import com.consolefire.xpenser.store.service.ReceiptService;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;

    @Override
    @Transactional(TxType.REQUIRES_NEW) // Ensure transactional behavior for database operations
    public ReceiptResponseDto createReceipt(ReceiptRequestDto requestDto) {
        log.debug("Attempting to create receipt for identifier: {}", requestDto.getIdentifier());

        log.debug("No existing receipt found. Proceeding with creation.");
        // Map DTO to Entity
        ReceiptEntity newReceiptEntity = receiptMapper.toEntity(requestDto);

        // Ensure generatedAt is set if DTO's timestamp was null (e.g., from "" in JSON)
        if (newReceiptEntity.getGeneratedAt() == null) {
            newReceiptEntity.setGeneratedAt(Instant.now());
        }

        // Set bidirectional relationships for nested entities before saving
        if (newReceiptEntity.getPayment() != null) {
            newReceiptEntity.getPayment().setReceipt(newReceiptEntity);
            if (newReceiptEntity.getPayment().getTax() != null) {
                newReceiptEntity.getPayment().getTax().setPayment(newReceiptEntity.getPayment());
                newReceiptEntity.getPayment().getTax().getTaxDetails()
                    .forEach(td -> td.setTax(newReceiptEntity.getPayment().getTax()));
            }
        }
        if (newReceiptEntity.getItems() != null) {
            newReceiptEntity.getItems().forEach(item -> item.setReceipt(newReceiptEntity));
        }

        ReceiptEntity receiptEntity = receiptRepository.save(newReceiptEntity);

        // Save the new entity
        return receiptMapper.toResponseDto(receiptEntity);
    }

    @Override
    @Transactional(TxType.REQUIRES_NEW) // Ensure transactional behavior for database operations
    public Optional<ReceiptResponseDto> updateReceiptById(Long id, ReceiptRequestDto requestDto) {
        ReceiptEntity receiptEntity = receiptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not Found"));
        if (null == requestDto) {
            return Optional.empty();
        }
        receiptMapper.updateEntityFromDto(requestDto, receiptEntity);
        receiptEntity.setId(id);
        return Optional.ofNullable(receiptMapper.toResponseDto(receiptRepository.save(receiptEntity)));
    }
}