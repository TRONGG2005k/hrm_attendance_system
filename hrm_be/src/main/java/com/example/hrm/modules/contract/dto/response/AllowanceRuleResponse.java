package com.example.hrm.modules.contract.dto.response;

import com.example.hrm.modules.organization.dto.response.PositionResponse;
import com.example.hrm.modules.organization.dto.response.SubDepartmentResponse;
import com.example.hrm.shared.enums.AllowanceCalculationType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AllowanceRuleResponse {

    String id;
    AllowanceResponse allowance;
    PositionResponse position;
    SubDepartmentResponse subDepartment;

    BigDecimal amount;
    AllowanceCalculationType calculationType;
    Boolean active;
}

