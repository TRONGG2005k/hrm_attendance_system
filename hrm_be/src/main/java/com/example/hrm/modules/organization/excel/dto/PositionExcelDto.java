package com.example.hrm.modules.organization.excel.dto;

import lombok.Data;

@Data
public class PositionExcelDto {
    private String code;
    private String name;
    private String description;
    private Boolean active; // true / false
}
