package com.example.hrm.modules.payroll.dto.request;


import com.example.hrm.shared.enums.PayrollStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollApprovalRequest {

    @NotNull
    Integer month;

    @NotNull
    Integer year;

    String comment; // ghi chú khi duyệt / từ chối

    @NotNull
    PayrollStatus status; // APPROVED hoặc REJECTED
}

