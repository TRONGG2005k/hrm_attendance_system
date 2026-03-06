package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.shared.enums.AttendanceEvaluation;
import com.example.hrm.shared.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceListResponse {
    private String id;

    private String employeeCode;
    private LocalDate workDate;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private long lateMinutes;
    private long earlyLeaveMinutes;

    private long otMinutes;
    private double otRate;
    private AttendanceEvaluation evaluation;
    private AttendanceStatus status;
}
