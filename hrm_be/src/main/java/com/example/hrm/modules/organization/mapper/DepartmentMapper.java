package com.example.hrm.modules.organization.mapper;

import com.example.hrm.modules.organization.dto.response.DepartmentResponse;
import com.example.hrm.modules.organization.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    
    @Mapping(target = "subDepartmentResponses", ignore = true)
    DepartmentResponse toResponse(Department department);


}
