package com.example.hrm.modules.contract.mapper;

import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.entity.Allowance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AllowanceMapper {

    AllowanceResponse toResponse(Allowance entity);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "id", ignore = true)
    Allowance toEntity(AllowanceRequest request);
}
