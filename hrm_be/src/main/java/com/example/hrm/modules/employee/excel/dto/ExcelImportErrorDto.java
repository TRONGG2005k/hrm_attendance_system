package com.example.hrm.modules.employee.excel.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExcelImportErrorDto {
    private int rowNumber;          // số dòng trong Excel (bắt đầu từ 1 hoặc 2)
    private List<String> errors;    // danh sách lỗi của dòng đó
}

