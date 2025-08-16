package com.consolefire.xpenser.expense.data.mapper;

import com.consolefire.xpenser.expense.data.dto.request.ItemRequestDto;
import com.consolefire.xpenser.expense.data.entiry.ItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {UnitValueMapper.class})
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "receipt", ignore = true) // Set on the Receipt side
    @Mapping(target = "quantity", source = "quantity") // Map quantity
    @Mapping(target = "price", source = "price") // Map price (BigDecimal to BigDecimal)
    @Mapping(target = "unitValue", source = "unitValue")
        // Map embedded UnitValue
    ItemEntity toEntity(ItemRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "receipt", ignore = true)
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "unitValue", source = "unitValue")
    void updateEntityFromDto(ItemRequestDto dto, @MappingTarget ItemEntity entity);
}