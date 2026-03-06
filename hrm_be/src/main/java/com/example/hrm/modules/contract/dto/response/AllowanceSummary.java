package com.example.hrm.modules.contract.dto.response;

import java.math.BigDecimal;

public record AllowanceSummary(
        String name,
        BigDecimal amount
) {}
