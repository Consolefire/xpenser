package com.consolefire.xpenser.store.service.impl;

import com.consolefire.xpenser.store.data.dto.request.ReceiptRequestDto;
import com.consolefire.xpenser.store.data.dto.response.ReceiptResponseDto;
import com.consolefire.xpenser.store.data.entity.ReceiptEntity;
import com.consolefire.xpenser.store.data.mapper.ReceiptMapper;
import com.consolefire.xpenser.store.data.repository.ReceiptRepository;
import com.consolefire.xpenser.store.exception.ReceiptExistsException;
import com.consolefire.xpenser.store.service.ReceiptService;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;

    @Override
    @Transactional(TxType.REQUIRES_NEW) // Ensure transactional behavior for database operations
    public Mono<ReceiptResponseDto> createReceipt(ReceiptRequestDto requestDto) {
        log.debug("Attempting to create receipt for identifier: {}", requestDto.getIdentifier());

        // Use Mono.defer and subscribeOn to offload blocking repository call
        return Mono.defer(() -> {
                log.debug("Checking for existing receipt with unique key: {}, {}, {}",
                    requestDto.getIdentifier(), requestDto.getMarketPlaceCode(), requestDto.getStoreCode());

                return Mono.justOrEmpty(receiptRepository.findByUniqueKey(
                    requestDto.getIdentifier(),
                    requestDto.getMarketPlaceCode(),
                    requestDto.getStoreCode()
                ));
            })
            .subscribeOn(Schedulers.boundedElastic()) // Offload blocking findByUniqueKey to a bounded elastic scheduler
            .flatMap(existingReceiptOpt -> {
                if (existingReceiptOpt != null) {
                    log.warn(
                        "Receipt with identifier '{}', marketplace '{}', and store '{}' already exists. Throwing exception.",
                        requestDto.getIdentifier(), requestDto.getMarketPlaceCode(), requestDto.getStoreCode());
                    // If receipt exists, throw the custom exception
                    return Mono.error(new ReceiptExistsException(
                        requestDto.getIdentifier(),
                        requestDto.getMarketPlaceCode(),
                        requestDto.getStoreCode()
                    ));
                } else {
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

                    // Save the new entity
                    return Mono.fromCallable(() -> receiptRepository.save(newReceiptEntity))
                        .subscribeOn(Schedulers.boundedElastic()) // Offload blocking save operation
                        .map(receiptMapper::toResponseDto) // Map saved entity to Response DTO
                        .doOnSuccess(dto -> log.info("Successfully created receipt with ID: {} and identifier: {}",
                            dto.getReceiptId(), dto.getIdentifier()));
                }
            })
            .doOnError(e -> log.error("Error during receipt creation for identifier {}: {}", requestDto.getIdentifier(),
                e.getMessage()));
    }

}