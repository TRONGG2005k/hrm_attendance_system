package com.example.hrm.modules.organization.excel.mapper;

import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.modules.organization.excel.dto.SubDepartmentExcelDto;
import org.springframework.stereotype.Component;

@Component
public class SubDepartmentExcelMapper {
    
    public SubDepartment toSubDepartment(SubDepartmentExcelDto dto, Department department){
        SubDepartment subDepartment = new SubDepartment();
        subDepartment.setName(dto.getName());
        subDepartment.setDepartment(department);
        subDepartment.setDescription(dto.getDescription());
        return subDepartment;
    }

    public SubDepartmentExcelDto toDto(SubDepartment subDepartment){
        SubDepartmentExcelDto dto = new SubDepartmentExcelDto();
        dto.setName(subDepartment.getName());
        dto.setDepartmentName(subDepartment.getDepartment().getName());
        dto.setDescription(subDepartment.getDescription());
        return dto;
    }
}
