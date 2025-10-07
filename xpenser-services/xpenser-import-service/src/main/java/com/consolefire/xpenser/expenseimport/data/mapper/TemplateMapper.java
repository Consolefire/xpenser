package com.consolefire.xpenser.expenseimport.data.mapper;

import com.consolefire.xpenser.expenseimport.api.model.CreateTemplateRequest;
import com.consolefire.xpenser.expenseimport.api.model.TemplateResponse;
import com.consolefire.xpenser.expenseimport.data.entity.Template;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    TemplateMapper INSTANCE = Mappers.getMapper(TemplateMapper.class);

    // Maps a Template entity to a TemplateResponse DTO.
    // The 'template' field in TemplateResponse is not present in the entity, so we ignore it.
    @Mapping(target = "template", ignore = true)
    TemplateResponse toResponse(Template template);

    // Maps a list of Template entities to a list of TemplateResponse DTOs.
    List<TemplateResponse> toResponseList(List<Template> templates);

    // Maps a CreateTemplateRequest DTO to a Template entity for creation.
    // Maps 'template' field from request to 'data' field in the entity.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "data", source = "template")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "recordVersion", ignore = true)
    Template toEntity(CreateTemplateRequest request);

    // Updates an existing Template entity with new data.
    // Used for mapping the request to the entity when updating.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true) // Name is immutable (updatable=false)
    @Mapping(target = "data", source = "template") // Map 'template' field from request to 'data' field in entity
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "recordVersion", ignore = true)
    void updateEntityFromRequest(CreateTemplateRequest request, @MappingTarget Template template);
}
