package com.example.hrm.modules.payroll.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollSummaryResponse {

    private BigDecimal grossSalary;

    private BigDecimal totalDeductions;

    private BigDecimal netSalary;
}
