package com.example.hrm.modules.attendance.dto.request;


import lombok.Data;

import java.time.LocalDateTime;

import com.example.hrm.shared.enums.BreakType;

@Data
public class BreakTimeRequest {
    private String attendanceId;
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private BreakType type;
}
