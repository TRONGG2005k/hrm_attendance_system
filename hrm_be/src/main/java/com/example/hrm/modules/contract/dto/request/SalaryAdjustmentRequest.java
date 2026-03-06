package com.example.hrm.modules.contract.dto.request;

import com.example.hrm.shared.enums.AdjustmentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAdjustmentRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotNull(message = "Loại điều chỉnh không được để trống")
    AdjustmentType type; // BONUS / PENALTY

    @NotNull(message = "Số tiền không được để trống")
    BigDecimal amount;

    @NotNull(message = "Ngày áp dụng không được để trống")
    LocalDate appliedDate;

    String description;
}
