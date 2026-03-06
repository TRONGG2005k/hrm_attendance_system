package com.example.hrm.modules.payroll.dto.request;

import com.example.hrm.shared.enums.PayrollApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollApprovalHistoryRequest {

    @NotNull(message = "Tháng không được để trống")
    Integer month;

    @NotNull(message = "Năm không được để trống")
    Integer year;

    Double totalAmount;

    String payrollSnapshot;

    String approvedById;

    @NotNull(message = "Trạng thái không được để trống")
    PayrollApprovalStatus status;

    String comment;
}
