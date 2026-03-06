package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class DeductionsResponse {

    private InsuranceDeductionResponse insurance;

    private BigDecimal personalIncomeTax;

    private List<SalaryAdjustmentResponse> penalties;

    private BigDecimal advanceSalary;

    private BigDecimal totalDeductions;
}

