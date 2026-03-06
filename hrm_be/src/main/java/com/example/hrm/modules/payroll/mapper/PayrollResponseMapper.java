package com.example.hrm.modules.payroll.mapper;

import com.example.hrm.modules.attendance.dto.response.AttendanceSummaryResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.contract.dto.response.AllowanceSummary;
import com.example.hrm.modules.contract.entity.SalaryAdjustment;
import com.example.hrm.modules.contract.mapper.SalaryAdjustmentMapper;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.*;
import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.shared.enums.AdjustmentType;
import com.example.hrm.shared.enums.AllowanceCalculationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PayrollResponseMapper {

    private final SalaryAdjustmentMapper salaryAdjustmentMapper;

    public AttendanceSummaryResponse toAttendanceSummary(
            List<Attendance> attendanceList,
            PayrollDetailResponse payrollDetail) {

        Map<LocalDate, List<Attendance>> attendanceByDate =
                attendanceList.stream()
                        .collect(Collectors.groupingBy(Attendance::getWorkDate));

        long lateDays = attendanceByDate.values().stream()
                .filter(list ->
                        list.stream().anyMatch(a -> a.getLateMinutes() > 0)
                )
                .count();

        long earlyLeaveDays = attendanceByDate.values().stream()
                .filter(list ->
                        list.stream().anyMatch(a -> a.getEarlyLeaveMinutes() > 0)
                )
                .count();


        return AttendanceSummaryResponse.builder()
                .expectedWorkingDays(attendanceByDate.size())
                .actualWorkingDays(payrollDetail.workingDays())
                .lateDays(lateDays)
                .earlyLeaveDays(earlyLeaveDays)
                .totalOtHours(
                        (long) attendanceList.stream()
                                .flatMap(a -> a.getAttendanceOTRates().stream())
                                .mapToDouble(AttendanceOTRate::getOtHours)
                                .sum()
                )
                .build();
    }

    public EarningsResponse toEarningsResponse(

            PayrollDetailResponse payrollDetail,
            List<SalaryAdjustment> salaryAdjustments) {

        var bonusResponses = salaryAdjustments.stream()
                .filter(a -> a.getType() == AdjustmentType.BONUS)
                .map(salaryAdjustmentMapper::toResponse)
                .toList();

        EarningsResponse earnings = new EarningsResponse();
        earnings.setBaseSalaryTotal(payrollDetail.baseSalaryTotal());
        earnings.setAllowances(payrollDetail.allowance());
        earnings.setOvertimePay(payrollDetail.otTotal());
        earnings.setBonuses(bonusResponses);
        earnings.setTotalEarnings(
                payrollDetail.baseSalaryTotal()
                        .add(payrollDetail.totalAllowance())
                        .add(payrollDetail.otTotal())
                        .add(payrollDetail.totalBonus())
        );
        return earnings;
    }

    public DeductionsResponse toDeductionsResponse(
            List<SalaryAdjustment> salaryAdjustments,
            PayrollDetailResponse payrollDetail) {

        var penaltyResponses = salaryAdjustments.stream()
                .filter(a -> a.getType() == AdjustmentType.PENALTY)
                .map(salaryAdjustmentMapper::toResponse)
                .toList();

        DeductionsResponse deductions = new DeductionsResponse();
        deductions.setPenalties(penaltyResponses);
        deductions.setInsurance(null);
        deductions.setPersonalIncomeTax(BigDecimal.ZERO);
        deductions.setAdvanceSalary(BigDecimal.ZERO);
        deductions.setTotalDeductions(payrollDetail.totalPenalty());
        return deductions;
    }

    public PayrollSummaryResponse toPayrollSummary(
            EarningsResponse earnings,
            DeductionsResponse deductions) {

        PayrollSummaryResponse summary = new PayrollSummaryResponse();
        summary.setGrossSalary(earnings.getTotalEarnings());
        summary.setTotalDeductions(deductions.getTotalDeductions());
        summary.setNetSalary(
                earnings.getTotalEarnings()
                        .subtract(deductions.getTotalDeductions())
        );
        return summary;
    }

    public PeriodResponse toPeriodResponse(PayrollRequest request) {
        return new PeriodResponse(request.getMonth(), request.getYear());
    }

    public EmployeePayrollResponse toEmployeeResponse(Employee employee) {
        return EmployeePayrollResponse.builder()
                .employeeId(employee.getId())
                .employeeCode(employee.getCode())
                .fullName(employee.getLastName() + " " + employee.getFirstName())
                .subDepartment(employee.getSubDepartment().getName())
                .build();
    }

    public PayrollMetadataResponse toMetadata() {
        PayrollMetadataResponse metadata = new PayrollMetadataResponse();
        metadata.setStatus("DRAFT");
        metadata.setVersion(1);
        metadata.setCalculatedAt(LocalDateTime.now());
        metadata.setCalculatedBy("SYSTEM");
        return metadata;
    }

    public PayrollListItemResponse toListResponse(PayrollResponse item){
        BigDecimal totalAllowance = BigDecimal.ZERO;
        for (
                Map.Entry<AllowanceCalculationType, AllowanceSummary> entry
                :
                item.getEarnings().getAllowances().entrySet()
        ) {
            totalAllowance = totalAllowance.add((entry.getValue().amount()));
        }

        return new PayrollListItemResponse(
                item.getPayrollId(),
                item.getEmployee().getEmployeeId(),
                item.getEmployee().getEmployeeCode(),
                item.getEmployee().getFullName(),
                item.getPeriod(),
                item.getEarnings().getBaseSalaryTotal(),
                item.getSummary().getNetSalary(),
                totalAllowance,
                item.getSummary().getTotalDeductions(),
                item.getStatus(),
                item.getMetadata().getCalculatedAt()
        );
    }

    public PayrollListItemResponse toListResponse(Payroll item){
//        String[] parts  = item.getMonth().split("-");

        var periodResponse = new PeriodResponse(item.getMonth().getYear(), item.getMonth().getMonthValue());
        return new PayrollListItemResponse(
                item.getId(),
                item.getEmployee().getId(),
                item.getEmployee().getCode(),
                item.getEmployee().getLastName() + item.getEmployee().getFirstName(),
                periodResponse,
                item.getBaseSalary(),
                item.getTotalSalary(),
                item.getAllowance(),
                item.getPenalty(),
                item.getStatus(),
                item.getCreatedAt()
        );
    }
}
