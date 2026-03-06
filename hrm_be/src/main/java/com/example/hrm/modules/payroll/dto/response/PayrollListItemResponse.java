package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.shared.enums.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayrollListItemResponse(
        String payrollId,
        String employeeId,
        String employeeCode,
        String fullName,
        PeriodResponse period,
        BigDecimal baseSalary,
        BigDecimal totalSalary,
        BigDecimal totalAllowance,
        BigDecimal totalDeductions,
        PayrollStatus status,
        LocalDateTime calculatedAt
) {}
