package com.consolefire.xpenser.expense.data.mapper;

import com.consolefire.xpenser.expense.data.TaxUnit;
import com.consolefire.xpenser.expense.data.dto.request.TaxUnitRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface TaxUnitMapper {

    TaxUnit toTaxUnit(TaxUnitRequestDto dto);

    void updateTaxUnitFromDto(TaxUnitRequestDto dto, @MappingTarget TaxUnit entity);
}