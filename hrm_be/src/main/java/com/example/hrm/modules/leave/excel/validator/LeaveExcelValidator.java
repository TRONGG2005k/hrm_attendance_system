package com.example.hrm.modules.leave.excel.validator;

import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.leave.excel.dto.LeaveImportRequestExcelDto;
import com.example.hrm.modules.leave.repository.LeaveRequestRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveExcelValidator {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ExcelHelper excelHelper;

    public List<String> validateLeave(LeaveImportRequestExcelDto dto, int rowNumber) {
        List<String> errors = new ArrayList<>();

        // ================= EMPLOYEE CODE =================
        if (excelHelper.isBlank(dto.getEmployeeCode())) {
            errors.add(error(rowNumber, "Mã nhân viên không được để trống"));
        } else if (!employeeRepository.existsByCodeAndIsDeletedFalse(dto.getEmployeeCode())) {
            errors.add(error(rowNumber, "Không tồn tại nhân viên: " + dto.getEmployeeCode()));
        }

        // ================= START DATE =================
        if (dto.getStartDate() == null) {
            errors.add(error(rowNumber, "Ngày bắt đầu không được để trống"));
        }

        // ================= END DATE =================
        if (dto.getEndDate() == null) {
            errors.add(error(rowNumber, "Ngày kết thúc không được để trống"));
        }

        // ================= DATE LOGIC =================
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                errors.add(error(rowNumber, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu"));
            }

            // if (excelHelper.isFutureDate(dto.getStartDate())) {
            // errors.add(error(rowNumber, "Ngày bắt đầu không được lớn hơn ngày hiện
            // tại"));
            // }

            // if (excelHelper.isFutureDate(dto.getEndDate())) {
            // errors.add(error(rowNumber, "Ngày kết thúc không được lớn hơn ngày hiện
            // tại"));
            // }
        }

        // ================= LEAVE TYPE =================
        if (dto.getType() == null) {
            errors.add(error(rowNumber, "Loại nghỉ không được để trống"));
        }

        // ================= DUPLICATE / OVERLAP CHECK =================
        if (!excelHelper.isBlank(dto.getEmployeeCode())
                && dto.getStartDate() != null
                && dto.getEndDate() != null) {

            boolean exists = leaveRequestRepository
                    .existsByEmployee_CodeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsDeletedFalse(
                            dto.getEmployeeCode(),
                            dto.getEndDate(),
                            dto.getStartDate());

            if (exists) {
                errors.add(error(rowNumber, "Khoảng thời gian nghỉ bị trùng với dữ liệu đã tồn tại"));
            }
        }

        return errors;
    }

    // ================== PRIVATE HELPERS ==================

    private String error(int rowNumber, String message) {
        return "Dòng " + rowNumber + ": " + message;
    }
}
