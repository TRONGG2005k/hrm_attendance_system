package com.example.hrm.modules.penalty.mapper;

import com.example.hrm.modules.penalty.dto.request.PenaltyRuleRequest;
import com.example.hrm.modules.penalty.dto.response.PenaltyRuleResponse;
import com.example.hrm.modules.penalty.entity.PenaltyRule;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PenaltyRuleMapper {

    // Create
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PenaltyRule toEntity(PenaltyRuleRequest request);

    // Update
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(PenaltyRuleRequest request, @MappingTarget PenaltyRule entity);

    // Response
    PenaltyRuleResponse toResponse(PenaltyRule entity);
}
