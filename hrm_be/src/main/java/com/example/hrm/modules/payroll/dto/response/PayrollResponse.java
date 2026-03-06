package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.modules.attendance.dto.response.AttendanceSummaryResponse;

import com.example.hrm.shared.enums.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollResponse {

    private String payrollId;

    private PeriodResponse period;

    private EmployeePayrollResponse employee;

    private AttendanceSummaryResponse attendanceSummary;

    private EarningsResponse earnings;

    private DeductionsResponse deductions;

    private PayrollSummaryResponse summary;

    private PayrollMetadataResponse metadata;

    private PayrollStatus status;
}
