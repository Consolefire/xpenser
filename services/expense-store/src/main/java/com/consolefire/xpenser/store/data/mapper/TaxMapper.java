package com.consolefire.xpenser.store.data.mapper;

import com.consolefire.xpenser.store.data.dto.request.TaxRequestDto;
import com.consolefire.xpenser.store.data.entity.TaxEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {TaxDetailMapper.class})
public interface TaxMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "payment", ignore = true) // Set on Payment side
    @Mapping(target = "taxDetails", source = "taxes") // Map taxes to taxDetails
    TaxEntity toEntity(TaxRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "taxDetails", source = "taxes")
    void updateEntityFromDto(TaxRequestDto dto, @MappingTarget TaxEntity entity);

    @AfterMapping
    default void setTaxOnTaxDetails(@MappingTarget TaxEntity taxEntity) {
        if (taxEntity.getTaxDetails() != null) {
            taxEntity.getTaxDetails().forEach(td -> td.setTax(taxEntity));
        }
    }
}