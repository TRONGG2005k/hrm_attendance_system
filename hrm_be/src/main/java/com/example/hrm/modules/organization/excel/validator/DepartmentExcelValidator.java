package com.example.hrm.modules.organization.excel.validator;

import com.example.hrm.modules.organization.excel.dto.DepartmentExcelDto;
// import com.example.hrm.modules.organization.repository.DepartmentRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DepartmentExcelValidator {
    // private final DepartmentRepository departmentRepository;
    private final ExcelHelper excelHelper;

    public List<String>  valid(DepartmentExcelDto dto, int row){
        List<String> errorCode = new ArrayList<>();

        if (excelHelper.isBlank(dto.getName())) {
            errorCode.add("dòng" + row +" tên ko được để trống");
        }
        return errorCode;
    }
}
