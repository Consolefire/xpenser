package com.consolefire.xpenser.expense.data.mapper;

import com.consolefire.xpenser.expense.data.dto.request.TaxDetailRequestDto;
import com.consolefire.xpenser.expense.data.entiry.TaxDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {TaxUnitMapper.class}) // Use TaxUnitMapper
public interface TaxDetailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "tax", ignore = true) // Set on the Tax side
    @Mapping(target = "taxUnit", source = "taxUnit") // Map embedded TaxUnit
    @Mapping(target = "taxableAmount", source = "taxableAmount")
    TaxDetailEntity toEntity(TaxDetailRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "tax", ignore = true)
    @Mapping(target = "taxUnit", source = "taxUnit")
    @Mapping(target = "taxableAmount", source = "taxableAmount")
    void updateEntityFromDto(TaxDetailRequestDto dto, @MappingTarget TaxDetailEntity entity);
}