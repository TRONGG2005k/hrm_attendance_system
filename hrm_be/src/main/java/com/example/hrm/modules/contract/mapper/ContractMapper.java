package com.example.hrm.modules.contract.mapper;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.ContractUpdateRequest;
import com.example.hrm.modules.contract.dto.response.ContractListResponse;
import com.example.hrm.modules.contract.dto.response.ContractResponse;
import com.example.hrm.modules.contract.entity.Contract;
import com.example.hrm.modules.employee.mapper.EmployeeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface ContractMapper {

    @Mapping(target = "fileAttachmentResponses", ignore = true)
    ContractResponse toResponse(Contract contract);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "files", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Contract toEntity(ContractRequest request);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "files", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "code", ignore = true)
    void ContractUpdateRequestToEntity(ContractUpdateRequest request, @MappingTarget Contract contract);

    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "employeeCode", ignore = true)
    @Mapping(target = "contractType", ignore = true)
    @Mapping(target = "contractCode", ignore = true)
    ContractListResponse toListResponse(Contract contract);
}
