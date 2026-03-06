package com.example.hrm.modules.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeriodResponse {
    private int month;
    private int year;
}
