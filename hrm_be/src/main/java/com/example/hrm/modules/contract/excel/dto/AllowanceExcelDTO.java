package com.example.hrm.modules.contract.excel.dto;

import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.shared.enums.AllowanceCalculationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllowanceExcelDTO {

    private String code;

    private String name;

    private String description;

    private Boolean active;

    private String positionCode;

    private String subDepartmentName;

    private BigDecimal amount;

    private AllowanceCalculationType calculationType;

    private Position position;
    private SubDepartment subDepartment;
}
