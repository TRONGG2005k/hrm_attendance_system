package com.example.hrm.modules.employee.dto.response;

import com.example.hrm.shared.enums.LeaveType;
import com.example.hrm.shared.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveResponse {

    String id;

    String employeeId;

    LeaveType type;

    LocalDate startDate;

    LocalDate endDate;

    Double days;

    String reason;

    LeaveStatus status;

    String approvedById;

    String approvalComment;

    LocalDateTime createdAt;

    LocalDateTime approvedAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
