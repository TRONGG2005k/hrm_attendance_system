package com.example.hrm.modules.attendance.dto.request;

import com.example.hrm.shared.enums.BreakType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BreakTimeBatchRequest {
    private String subDepartmentId; // ID phòng ban
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private BreakType type;
}
