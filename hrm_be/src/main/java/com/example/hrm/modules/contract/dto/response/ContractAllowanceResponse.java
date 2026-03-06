package com.example.hrm.modules.contract.dto.response;

import lombok.Data;

@Data
public class ContractAllowanceResponse {

    String allowanceId;
    String allowanceName;
    Double amount;
    String calculationType;
}
