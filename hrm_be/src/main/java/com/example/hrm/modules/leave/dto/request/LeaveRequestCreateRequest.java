package com.example.hrm.modules.leave.dto.request;

import com.example.hrm.shared.enums.LeaveType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestCreateRequest {
    @NotNull
    String employeeId;
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;
    @NotNull
    LeaveType type;
    @NotBlank
    String reason;
}
