package com.example.hrm.modules.organization.excel.mapper;

import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.excel.dto.DepartmentExcelDto;
import org.springframework.stereotype.Component;


@Component
public class DepartmentExcelMapper {
    public Department toDepartment(DepartmentExcelDto dto){
        Department department = new Department();
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        return department;
    }

    public DepartmentExcelDto toDto(Department department){
        return new DepartmentExcelDto(department.getName(), department.getDescription());
    }
}
