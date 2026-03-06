package com.example.hrm.modules.organization.excel.validator;

import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.excel.dto.SubDepartmentExcelDto;
import com.example.hrm.modules.organization.repository.DepartmentRepository;
// import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
// import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubDepartmentExcelValidator {
    private final DepartmentRepository departmentRepository;
    // private final SubDepartmentRepository subDepartmentRepository;
    private final ExcelHelper excelHelper;

    public List<String> valid(SubDepartmentExcelDto dto, int row) {
        List<String> errors = new ArrayList<>();

        if (excelHelper.isBlank(dto.getName())) {
            errors.add("Dòng " + row + ": tên sub-department không được để trống");
        }

        if (excelHelper.isBlank(dto.getDepartmentName())) {
            errors.add("Dòng " + row + ": departmentName không được để trống");
        } else {
            // Kiểm tra department có tồn tại không
            Department departmentOpt = departmentRepository.findByNameAndIsDeletedFalse(dto.getDepartmentName());
            if (departmentOpt == null) {
                errors.add("Dòng " + row + ": department '" + dto.getDepartmentName() + "' không tồn tại");
            }
        }

        return errors;
    }
}
