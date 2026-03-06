package com.example.hrm.modules.payroll.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PayrollListResponse(
        String period,
        BigDecimal totalPayrollAmount,
        List<PayrollListItemResponse> payrolls
) {}
