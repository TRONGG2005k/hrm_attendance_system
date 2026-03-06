package com.example.hrm.modules.contract.excel.dto;

import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.shared.enums.ContractType;
import com.example.hrm.shared.enums.SalaryContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractExcelDto {

    /**
     * employee_code
     * map → Employee.id
     */
    String employeeCode;

    /**
     * contract_code
     * map → Contract.code
     */
    String contractCode;

    /**
     * contract_type
     * map → Contract.type
     */
    ContractType contractType;

    /**
     * sign_date
     * map → Contract.signDate
     */
    LocalDate signDate;

    /**
     * start_date
     * map → Contract.startDate
     */
    LocalDate startDate;

    /**
     * end_date
     * map → Contract.endDate
     */
    LocalDate endDate;

    /**
     * contract_status
     * map → Contract.status
     */
    ContractStatus contractStatus;

    /**
     * base_salary
     * map → SalaryContract.baseSalary
     */
    BigDecimal baseSalary;

    /**
     * salary_coefficient
     * map → SalaryContract.salaryCoefficient
     */
    Double salaryCoefficient;

    /**
     * salary_effective_date
     * map → SalaryContract.effectiveDate
     */
    LocalDate salaryEffectiveDate;

    /**
     * salary_status
     * map → SalaryContract.status
     */
    SalaryContractStatus salaryStatus;

    /**
     * note
     * map → Contract.note
     */
    String note;
}

