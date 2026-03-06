package com.example.hrm.modules.contract.dto.request;

import com.example.hrm.shared.enums.SalaryContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryContractRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotBlank(message = "Mã hợp đồng không được để trống")
    String contractId;

    BigDecimal baseSalary;

    Double salaryCoefficient;

    @NotNull(message = "Ngày hiệu lực không được để trống")
    LocalDate effectiveDate;

    @NotNull(message = "Trạng thái không được để trống")
    SalaryContractStatus status;
}
