package com.example.hrm.modules.organization.excel.validator;

// import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.excel.dto.PositionExcelDto;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PositionExcelValidator {
    private final PositionRepository positionRepository;
    private final ExcelHelper excelHelper;

    public List<String> valid(PositionExcelDto dto, int row) {
        List<String> errors = new ArrayList<>();

        // Validate code
        if (excelHelper.isBlank(dto.getCode())) {
            errors.add("Dòng " + row + ": Mã chức vụ không được để trống");
        } else if (dto.getCode().length() > 50) {
            errors.add("Dòng " + row + ": Mã chức vụ không được vượt quá 50 ký tự");
        } else {
            // Check for duplicate code
            var existingByCode = positionRepository.findByCodeAndIsDeletedFalse(dto.getCode());
            if (existingByCode.isPresent()) {
                errors.add("Dòng " + row + ": Mã chức vụ '" + dto.getCode() + "' đã tồn tại");
            }
        }

        // Validate name
        if (excelHelper.isBlank(dto.getName())) {
            errors.add("Dòng " + row + ": Tên chức vụ không được để trống");
        } else if (dto.getName().length() > 100) {
            errors.add("Dòng " + row + ": Tên chức vụ không được vượt quá 100 ký tự");
        } else {
            // Check for duplicate name
            var existingByName = positionRepository.findByNameAndIsDeletedFalse(dto.getName());
            if (existingByName.isPresent()) {
                errors.add("Dòng " + row + ": Tên chức vụ '" + dto.getName() + "' đã tồn tại");
            }
        }

        // Validate description length if provided
        if (dto.getDescription() != null && dto.getDescription().length() > 500) {
            errors.add("Dòng " + row + ": Mô tả không được vượt quá 500 ký tự");
        }

        // Validate active status if provided
        if (dto.getActive() == null) {
            errors.add("Dòng " + row + ": Trạng thái hoạt động không được để trống (true/false)");
        }

        return errors;
    }
}
