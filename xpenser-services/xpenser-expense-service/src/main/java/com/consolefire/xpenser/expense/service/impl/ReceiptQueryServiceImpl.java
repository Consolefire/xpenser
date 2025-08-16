package com.consolefire.xpenser.expense.service.impl;

import com.consolefire.xpenser.expense.data.dto.query.QueryRequestDto;
import com.consolefire.xpenser.expense.data.dto.query.ReceiptQueryProperties;
import com.consolefire.xpenser.expense.data.dto.response.ReceiptDetailResponseDto;
import com.consolefire.xpenser.expense.data.entiry.ReceiptEntity;
import com.consolefire.xpenser.expense.data.entiry.ReceiptEntity.Fields;
import com.consolefire.xpenser.expense.data.exceptions.UnexpectedCountOfRecordsFoundException;
import com.consolefire.xpenser.expense.data.mapper.ReceiptMapper;
import com.consolefire.xpenser.expense.data.repository.ReceiptRepository;
import com.consolefire.xpenser.expense.service.ReceiptQueryService;
import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptQueryServiceImpl implements ReceiptQueryService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final EntityManager entityManager;

    @Override
    public Optional<ReceiptDetailResponseDto> fetchWithIdentifier(String marketPlaceCode, String storeCode,
        String identifier) {
        Optional<ReceiptEntity> receiptEntity = receiptRepository.findByIdentifierAndMarketPlaceCodeAndStoreCode(
            identifier, marketPlaceCode, storeCode);
        return Optional.ofNullable(receiptEntity.map(receiptMapper::fromEntityToReceiptDetailResponseDto).orElse(null));
    }

    @Override
    public List<ReceiptDetailResponseDto> fetchAllReceipts(QueryRequestDto queryRequestDto) {
        log.info("Fetching all receipts with query: {}", queryRequestDto);
        List<ReceiptEntity> entities = receiptRepository.findAll();

        return entities.stream()
            .map(receiptMapper::fromEntityToReceiptDetailResponseDto)
            .collect(Collectors.toList());
    }


    @Override
    public Optional<ReceiptDetailResponseDto> querySingleRecord(
        QueryRequestDto<ReceiptQueryProperties> queryRequestDto) {
        ReceiptQueryProperties queryProperties = queryRequestDto.getQueryProperties();
        if (queryProperties == null) {
            return Optional.empty();
        }

        ExampleMatcher matcher = ExampleMatcher.matching();
        if (null != queryProperties.getIdentifier() && !queryProperties.getIdentifier().isBlank()) {
            matcher = matcher.withMatcher(Fields.identifier.name(), pm -> pm.exact());
        }

        if (null != queryProperties.getMarketPlaceCode() && !queryProperties.getMarketPlaceCode().isBlank()) {
            matcher = matcher.withMatcher(Fields.marketPlaceCode.name(), pm -> pm.exact());
        }

        if (null != queryProperties.getStoreCode() && !queryProperties.getStoreCode().isBlank()) {
            matcher = matcher.withMatcher(Fields.storeCode.name(), pm -> pm.exact());
        }

        if (null != queryProperties.getGeneratedAt()) {
            matcher = matcher.withMatcher(Fields.generatedAt.name(), m -> m.exact());
        }

        ReceiptEntity receiptEntity = new ReceiptEntity();

        Example<ReceiptEntity> receiptEntityExample = Example.of(receiptEntity, matcher);

        Collection<ReceiptEntity> receiptEntities = receiptRepository.findAll(receiptEntityExample);
        if (null == receiptEntities || receiptEntities.isEmpty()) {
            return Optional.empty();
        }
        if (receiptEntities.size() > 1) {
            throw new UnexpectedCountOfRecordsFoundException(1, receiptEntities.size());
        }
        return Optional.of(receiptMapper.fromEntityToReceiptDetailResponseDto(receiptEntities.iterator().next()));
    }

    @Override
    public List<ReceiptDetailResponseDto> queryForList(QueryRequestDto<ReceiptQueryProperties> queryRequestDto) {
        return List.of();
    }
}