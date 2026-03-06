package com.example.hrm.modules.contract.mapper;

import com.example.hrm.modules.contract.dto.request.SalaryContractRequest;
import com.example.hrm.modules.contract.dto.response.SalaryContractResponse;
import com.example.hrm.modules.contract.entity.SalaryContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SalaryContractMapper {

    SalaryContractMapper INSTANCE = Mappers.getMapper(SalaryContractMapper.class);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "contract", ignore = true)
    @Mapping(source = "employeeId", target = "employee.id")
    SalaryContract toEntity(SalaryContractRequest request);

    @Mapping(target = "contractId", ignore = true)
    @Mapping(source = "employee.id", target = "employeeId")
    SalaryContractResponse toResponse(SalaryContract entity);
}
