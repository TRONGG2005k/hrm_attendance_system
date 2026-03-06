package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.shared.enums.BreakType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BreakTimeResponse {
    private String id;
    private String attendanceId;
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private BreakType type;
}
