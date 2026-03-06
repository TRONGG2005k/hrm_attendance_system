package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.organization.dto.response.SubDepartmentResponse;
import com.example.hrm.modules.organization.entity.SubDepartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubDepartmentMapper {

    @Mapping(source = "department.id", target = "departmentId")
    SubDepartmentResponse toResponse(SubDepartment subDepartment);
}
