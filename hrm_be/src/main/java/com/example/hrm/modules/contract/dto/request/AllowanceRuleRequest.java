package com.example.hrm.modules.contract.dto.request;

import com.example.hrm.shared.enums.AllowanceCalculationType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AllowanceRuleRequest {

    String allowanceId;
    String positionId;       // nullable
    String subDepartmentId; // nullable

    BigDecimal amount;

    AllowanceCalculationType calculationType;
}
