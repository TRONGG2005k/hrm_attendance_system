package com.example.hrm.modules.contract.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.shared.enums.ContractType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractUpdateRequest {
    @NotNull(message = "Loại hợp đồng không được để trống")
    ContractType type;

    LocalDate signDate;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDate startDate;

    LocalDate endDate;

    Double baseSalary;

    @NotNull(message = "Trạng thái không được để trống")
    ContractStatus status;

    SalaryContractRequest salaryContract;

    String note;
}
