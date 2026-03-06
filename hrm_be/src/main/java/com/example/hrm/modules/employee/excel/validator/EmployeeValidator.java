package com.example.hrm.modules.employee.excel.validator;

import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor

public class EmployeeValidator {

    private final SubDepartmentRepository subDepartmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final ExcelHelper excelHelper;

    public List<String> validateEmployee(EmployeeExcelImportDto dto, int rowNumber) {
        List<String> errors = new ArrayList<>();

        // Code
        if (excelHelper.isBlank(dto.getCode())) {
            errors.add(error(rowNumber, "Code không được để trống"));
        } else if (employeeRepository.existsByCodeAndIsDeletedFalse(dto.getCode())) {
            errors.add(error(rowNumber, "Mã nhân viên đã tồn tại: " + dto.getCode()));
        }

        // First name
        if (excelHelper.isBlank(dto.getFirstName())) {
            errors.add(error(rowNumber, "First name không được để trống"));
        }

        // Last name
        if (excelHelper.isBlank(dto.getLastName())) {
            errors.add(error(rowNumber, "Last name không được để trống"));
        }

        // Email
        if (excelHelper.isBlank(dto.getEmail())) {
            errors.add(error(rowNumber, "Email không được để trống"));
        } else if (!excelHelper.isValidEmail(dto.getEmail())) {
            errors.add(error(rowNumber, "Email không hợp lệ"));
        }

        // Phone
        if (!excelHelper.isBlank(dto.getPhone()) && !excelHelper.isValidPhone(dto.getPhone())) {
            errors.add(error(rowNumber, "Số điện thoại không hợp lệ"));
        }

        // Gender
        if (excelHelper.isBlank(dto.getGender())) {
            errors.add(error(rowNumber, "Gender không được để trống"));
        } else if (!excelHelper.isValidEnum(Gender.class, dto.getGender())) {
            errors.add(error(rowNumber, "Gender không hợp lệ"));
        }

        // Status
        if (!excelHelper.isBlank(dto.getStatus())
                && !excelHelper.isValidEnum(EmployeeStatus.class, dto.getStatus())) {
            errors.add(error(rowNumber, "Status không hợp lệ"));
        }

        // Date of birth
        if (dto.getDateOfBirth() != null && excelHelper.isFutureDate(dto.getDateOfBirth())) {
            errors.add(error(rowNumber, "Ngày sinh không được lớn hơn ngày hiện tại"));
        }

        // Join date
        if (dto.getJoinDate() != null && excelHelper.isFutureDate(dto.getJoinDate())) {
            errors.add(error(rowNumber, "Ngày vào làm không được lớn hơn ngày hiện tại"));
        }

        // Department
        if (excelHelper.isBlank(dto.getDepartmentName())) {
            errors.add(error(rowNumber, "Tên phòng ban không được để trống"));
        } else if (!subDepartmentRepository.existsByNameAndIsDeletedFalse(dto.getDepartmentName())) {
            errors.add(error(rowNumber, "Không tồn tại phòng ban: " + dto.getDepartmentName()));
        }

        // Position
        if (excelHelper.isBlank(dto.getPositionName())) {
            errors.add(error(rowNumber, "Tên chức vụ không được để trống"));
        } else if (!positionRepository.existsByNameAndIsDeletedFalse(dto.getPositionName())) {
            errors.add(error(rowNumber, "Không tồn tại chức vụ: " + dto.getPositionName()));
        }

        return errors;
    }

    // ================== PRIVATE HELPERS ==================

    private String error(int rowNumber, String message) {
        return "Dòng " + rowNumber + ": " + message;
    }
}
