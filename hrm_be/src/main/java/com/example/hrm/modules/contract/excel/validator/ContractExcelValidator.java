package com.example.hrm.modules.contract.excel.validator;

import com.example.hrm.modules.contract.excel.dto.ContractExcelDto;
import com.example.hrm.modules.contract.repository.ContractRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractExcelValidator {

    private final EmployeeRepository employeeRepository;
    private final ContractRepository contractRepository;
    private final ExcelHelper excelHelper;

    public List<String> validateContract(ContractExcelDto dto, int rowNumber) {

        List<String> errors = new ArrayList<>();

        // EMPLOYEE
        if (excelHelper.isBlank(dto.getEmployeeCode())) {
            errors.add(error(rowNumber, "Mã nhân viên không được để trống"));
        } else if (!employeeRepository.existsByCodeAndIsDeletedFalse(dto.getEmployeeCode())) {
            errors.add(error(rowNumber, "Không tồn tại nhân viên: " + dto.getEmployeeCode()));
        }

        // CONTRACT CODE
        if (excelHelper.isBlank(dto.getContractCode())) {
            errors.add(error(rowNumber, "Mã hợp đồng không được để trống"));
        }
        else if (contractRepository.existsByEmployee_CodeAndCodeAndIsDeletedFalse(
                dto.getEmployeeCode(),
                dto.getContractCode()
        )) {
            errors.add(error(rowNumber, "Hợp đồng đã tồn tại: " + dto.getContractCode()));
        }

        // CONTRACT TYPE
        if (dto.getContractType() == null) {
            errors.add(error(rowNumber, "Loại hợp đồng không được để trống"));
        }

        // SIGN DATE
        if (dto.getSignDate() == null) {
            errors.add(error(rowNumber, "Ngày ký hợp đồng không được để trống"));
        }

        // START DATE
        if (dto.getStartDate() == null) {
            errors.add(error(rowNumber, "Ngày bắt đầu không được để trống"));
        }

        // END DATE LOGIC
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                errors.add(error(rowNumber, "Ngày kết thúc phải >= ngày bắt đầu"));
            }
        }

        // CONTRACT STATUS
        if (dto.getContractStatus() == null) {
            errors.add(error(rowNumber, "Trạng thái hợp đồng không được để trống"));
        }

        // BASE SALARY
        if (dto.getBaseSalary() == null) {
            errors.add(error(rowNumber, "Lương cơ bản không được để trống"));
        }
        else if (!excelHelper.isNonNegativeNumber(dto.getBaseSalary())) {
            errors.add(error(rowNumber, "Lương cơ bản phải >= 0"));
        }

        // SALARY COEFFICIENT
        if (dto.getSalaryCoefficient() != null &&
                !excelHelper.isNonNegativeNumber(dto.getSalaryCoefficient())) {

            errors.add(error(rowNumber, "Hệ số lương phải >= 0"));
        }

        // SALARY EFFECTIVE DATE
        if (dto.getSalaryEffectiveDate() == null) {
            errors.add(error(rowNumber, "Ngày hiệu lực lương không được để trống"));
        }
        else if (dto.getStartDate() != null &&
                dto.getSalaryEffectiveDate().isBefore(dto.getStartDate())) {

            errors.add(error(rowNumber, "Ngày hiệu lực lương phải >= ngày bắt đầu hợp đồng"));
        }

        // SALARY STATUS
        if (dto.getSalaryStatus() == null) {
            errors.add(error(rowNumber, "Trạng thái lương không được để trống"));
        }

        return errors;
    }


    // ================== PRIVATE HELPERS ==================

    private String error(int rowNumber, String message) {
        return "Dòng " + rowNumber + ": " + message;
    }
}
