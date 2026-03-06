package com.example.hrm.modules.contract.dto.response;

import com.example.hrm.shared.enums.SalaryContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryContractResponse {

    String id;

    String employeeId;

    String contractId;

    BigDecimal baseSalary;

    BigDecimal allowance;

    Double salaryCoefficient;

    LocalDate effectiveDate;

    SalaryContractStatus status;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
