package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.modules.contract.dto.response.AllowanceSummary;
import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.shared.enums.AllowanceCalculationType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class EarningsResponse {

    private BigDecimal baseSalaryTotal;

    private BigDecimal overtimePay;

    private Map<AllowanceCalculationType, AllowanceSummary> allowances;

    private List<SalaryAdjustmentResponse> bonuses;

    private BigDecimal totalEarnings;
}
