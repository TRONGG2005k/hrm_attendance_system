package com.example.hrm.modules.payroll.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BonusResponse {
    private String code;
    private String name;
    private BigDecimal amount;
}
