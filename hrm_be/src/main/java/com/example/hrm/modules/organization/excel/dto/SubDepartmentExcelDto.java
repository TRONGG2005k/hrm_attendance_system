package com.example.hrm.modules.organization.excel.dto;

import lombok.Data;

@Data
public class SubDepartmentExcelDto {
    private String name;
    private String departmentName; // liên kết Department
    private String description;
}
