package com.example.hrm.modules.contract.mapper;

import com.example.hrm.modules.contract.dto.request.SalaryAdjustmentRequest;
import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.modules.contract.entity.SalaryAdjustment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalaryAdjustmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true) // IMPORTANT
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SalaryAdjustment toEntity(SalaryAdjustmentRequest request);

    @Mapping(source = "employee.id", target = "employeeId")
    SalaryAdjustmentResponse toResponse(SalaryAdjustment entity);
}

