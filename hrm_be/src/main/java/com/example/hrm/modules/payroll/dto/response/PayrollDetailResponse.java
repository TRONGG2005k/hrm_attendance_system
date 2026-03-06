package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.modules.contract.dto.response.AllowanceSummary;
import com.example.hrm.shared.enums.AllowanceCalculationType;

import java.math.BigDecimal;
import java.util.Map;

public record PayrollDetailResponse(
        BigDecimal totalSalary,
        BigDecimal baseSalaryTotal,
        BigDecimal otTotal,
        Map<AllowanceCalculationType, AllowanceSummary> allowance,
        BigDecimal totalAllowance,
        BigDecimal totalBonus,
        BigDecimal totalPenalty,
        BigDecimal fullAttendanceBonus,
        long workingDays
) {}
