package com.example.hrm.modules.payroll.dto.response;

import lombok.Data;

@Data
public class OvertimeSummaryResponse {
    private Integer weekdayHours;
    private Integer weekendHours;
    private Integer holidayHours;
}
