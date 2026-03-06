package com.example.hrm.modules.leave.excel.dto;

import com.example.hrm.shared.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveImportRequestExcelDto {

    private String employeeCode;

    private LocalDate startDate;

    private LocalDate endDate;

    private LeaveType type;

    private String reason;
}
