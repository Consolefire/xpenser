package com.consolefire.xpenser.expense.data.mapper;

import com.consolefire.xpenser.expense.data.dto.request.PaymentRequestDto;
import com.consolefire.xpenser.expense.data.entiry.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {TaxMapper.class})
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "receipt", ignore = true) // Set on the Receipt side
    @Mapping(target = "tax", source = "tax")
    PaymentEntity toEntity(PaymentRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    @Mapping(target = "tax", source = "tax")
    void updateEntityFromDto(PaymentRequestDto dto, @MappingTarget PaymentEntity entity);
}