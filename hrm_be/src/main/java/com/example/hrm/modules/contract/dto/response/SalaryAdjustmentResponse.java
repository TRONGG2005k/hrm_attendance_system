package com.example.hrm.modules.contract.dto.response;

import com.example.hrm.shared.enums.AdjustmentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAdjustmentResponse {

    String id;

    String employeeId;

    AdjustmentType type;

    BigDecimal amount;

    LocalDate appliedDate;

    LocalDateTime createdAt;

    String description;

}
