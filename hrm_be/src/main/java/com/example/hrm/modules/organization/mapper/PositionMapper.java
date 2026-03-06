package com.example.hrm.modules.organization.mapper;

import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.dto.request.PositionRequest;
import com.example.hrm.modules.organization.dto.response.PositionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionResponse toResponse(Position entity);


    @Mapping(target = "id", ignore = true)
    Position toEntity(PositionRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PositionRequest request, @MappingTarget Position entity);
}
