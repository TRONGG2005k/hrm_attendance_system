package com.example.hrm.modules.leave.dto.request;

import com.example.hrm.shared.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LeaveRequestApprovalRequest {
    @NotNull
    LeaveStatus status; // APPROVED hoặc REJECTED
    String comment;
}
