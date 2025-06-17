package com.consolefire.xpenser.store.data.mapper;

import com.consolefire.xpenser.store.data.UnitValue;
import com.consolefire.xpenser.store.data.dto.request.UnitValueRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UnitValueMapper {

    UnitValue toUnitValue(UnitValueRequestDto dto);

    UnitValueRequestDto toRequestDto(UnitValue unitValue);

    void updateUnitValueFromDto(UnitValueRequestDto dto, @MappingTarget UnitValue entity);
}