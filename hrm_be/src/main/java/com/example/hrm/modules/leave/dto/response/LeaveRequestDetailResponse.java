package com.example.hrm.modules.leave.dto.response;

import com.example.hrm.shared.enums.LeaveStatus;
import com.example.hrm.shared.enums.LeaveType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequestDetailResponse {
    String id;
    String employeeId;
    String employeeCode;
    String fullName;
    LocalDate startDate;
    LocalDate endDate;
    LeaveType type;
    LeaveStatus status;
    String reason;
    LocalDateTime approvedAt;
    String approvedById;
    String approvedByName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
