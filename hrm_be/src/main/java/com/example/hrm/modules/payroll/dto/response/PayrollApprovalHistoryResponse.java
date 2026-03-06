package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.shared.enums.PayrollApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollApprovalHistoryResponse {

    String id;

    Integer month;

    Integer year;

    Double totalAmount;

    String payrollSnapshot;

    String approvedById;

    PayrollApprovalStatus status;

    String comment;

    LocalDateTime approvedAt;

    LocalDateTime createdAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
