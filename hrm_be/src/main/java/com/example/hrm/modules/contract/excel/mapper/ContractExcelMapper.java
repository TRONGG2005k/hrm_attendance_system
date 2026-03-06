package com.example.hrm.modules.contract.excel.mapper;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.SalaryContractRequest;
import com.example.hrm.modules.contract.excel.dto.ContractExcelDto;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractExcelMapper {

    private final ExcelHelper excelHelper;
    private final EmployeeRepository employeeRepository;

    public List<ContractExcelDto> toDto(Sheet sheet) {

        List<ContractExcelDto> dtos = new ArrayList<>();

        // row 0 là header → bắt đầu từ 1
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            ContractExcelDto dto = new ContractExcelDto();

            // 0 - employee_code
            dto.setEmployeeCode(
                    excelHelper.getString(row.getCell(0))
            );

            // 1 - contract_code
            dto.setContractCode(
                    excelHelper.getString(row.getCell(1))
            );

            // 2 - contract_type
            dto.setContractType(
                    excelHelper.getContractType(row.getCell(2))
            );

            // 3 - sign_date
            dto.setSignDate(
                    excelHelper.getLocalDate(row.getCell(3))
            );

            // 4 - start_date
            dto.setStartDate(
                    excelHelper.getLocalDate(row.getCell(4))
            );

            // 5 - end_date
            dto.setEndDate(
                    excelHelper.getLocalDate(row.getCell(5))
            );

            // 6 - contract_status
            dto.setContractStatus(
                    excelHelper.getContractStatus(row.getCell(6))
            );

            // 7 - base_salary
            dto.setBaseSalary(
                    excelHelper.getBigDecimal(row.getCell(7))
            );

            // 8 - salary_coefficient
            dto.setSalaryCoefficient(
                    excelHelper.getDouble(row.getCell(8))
            );

            // 9 - salary_effective_date
            dto.setSalaryEffectiveDate(
                    excelHelper.getLocalDate(row.getCell(9))
            );

            // 10 - salary_status
            dto.setSalaryStatus(
                    excelHelper.getSalaryContractStatus(row.getCell(10))
            );

            // 11 - note
            dto.setNote(
                    excelHelper.getString(row.getCell(11))
            );

            dtos.add(dto);
        }

        return dtos;
    }

    public ContractRequest toRequest(ContractExcelDto dto) {

        var employee = employeeRepository.findByCodeAndIsDeletedFalse(dto.getEmployeeCode())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        // Tạo ContractRequest
        return ContractRequest.builder()
                .employeeId(employee.getId())
                .code(dto.getContractCode())
                .type(dto.getContractType())
                .signDate(dto.getSignDate())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getContractStatus())
                .note(dto.getNote())
                .salaryContract( SalaryContractRequest.builder()
                        .employeeId(employee.getId())
                        // contractId sẽ được set sau khi Contract được tạo,
                        // nhưng vẫn có thể set tạm bằng contractCode
                        .contractId(dto.getContractCode())
                        .baseSalary(dto.getBaseSalary())
                        .salaryCoefficient(dto.getSalaryCoefficient())
                        .effectiveDate(dto.getSalaryEffectiveDate())
                        .status(dto.getSalaryStatus())
                        .build())
                .build();
    }

}
