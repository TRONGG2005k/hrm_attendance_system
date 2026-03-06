package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.contract.entity.SalaryAdjustment;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import com.example.hrm.modules.contract.service.SalaryAdjustmentService;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.PayrollCalculator;
import com.example.hrm.modules.payroll.PayrollPeriodCalculator;
import com.example.hrm.modules.payroll.dto.request.PayrollApprovalRequest;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.*;
import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.modules.payroll.entity.PayrollApprovalHistory;
import com.example.hrm.modules.payroll.mapper.PayrollResponseMapper;
import com.example.hrm.modules.payroll.repository.PayrollApprovalHistoryRepository;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.enums.PayrollApprovalStatus;
import com.example.hrm.shared.enums.PayrollStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final SalaryContractRepository salaryContractRepository;
    private final SalaryAdjustmentService salaryAdjustmentService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollCycleService payrollCycleService;
    private final PayrollCalculator payrollCalculator;
    private final AllowanceRuleRepository allowanceRuleRepository;
    private final PayrollResponseMapper payrollResponseMapper;
    private final PayrollApprovalHistoryRepository payrollApprovalHistoryRepository;
    private final UserAccountRepository userAccountRepository;
    private final ObjectMapper objectMapper;

//    @Transactional
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public PayrollResponse create(PayrollRequest request) {

        var cycle = payrollCycleService.getActive();
        var period = calculatePeriod(cycle, request.getYear(), request.getMonth());

        var employee = getEmployee(request.getEmployeeId());
        checkPayrollExists(employee.getId(), request.getYear(), request.getMonth());

        var salaryContract = getSalaryContract(employee);
        var attendanceList = getAttendanceList(employee, period);
        var salaryAdjustments = getSalaryAdjustments(employee, period);
        var allowance = getAllowance(employee);
        var payrollDetail = calculatePayrollDetail(salaryContract, attendanceList, salaryAdjustments, allowance, cycle);

        Payroll payroll = buildAndSavePayroll(employee, request, payrollDetail);

        return buildPayrollResponse(request, employee, attendanceList, salaryAdjustments, payrollDetail, payroll.getId(), PayrollStatus.DRAFT);
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public List<PayrollListItemResponse> createForAllEmployees(int month, int year) {

        var employees = employeeRepository.findAllByIsDeletedFalse();
        List<PayrollListItemResponse> results = new ArrayList<>();

        for (var employee : employees) {
            try {
                PayrollRequest request = new PayrollRequest();
                request.setEmployeeId(employee.getId());
                request.setMonth(month);
                request.setYear(year);

                PayrollResponse response = create(request);
                results.add(payrollResponseMapper.toListResponse(response));

            } catch (AppException ex) {
                if (ex.getErrorCode() == ErrorCode.PAYROLL_ALREADY_EXISTS) {
                    // Bỏ qua nhân viên đã có payroll trong kỳ
                    continue;
                }
                // Lỗi khác thì vẫn ném ra
                throw ex;
            }
        }

        return results;
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<PayrollListItemResponse> getAll(int page, int size){
        var listResponse = payrollRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return listResponse.map(payrollResponseMapper::toListResponse);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<PayrollListItemResponse> getAllByEmployeeId(String employeeId, int page, int size){
        var listResponse = payrollRepository.findByEmployeeIdAndIsDeletedFalse(employeeId, PageRequest.of(page, size));
        return listResponse.map(payrollResponseMapper::toListResponse);
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<PayrollListItemResponse> getAllByMouth(int page, int size, int month, int year){


        var listResponse = payrollRepository
                .findByIsDeletedFalseAndMonth(
                        YearMonth.of(year, month),
                        PageRequest.of(page, size)
                );

        return listResponse.map(payrollResponseMapper::toListResponse);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public PayrollResponse getById(String payrollId){
        Payroll payroll = payrollRepository.findByIdAndIsDeletedFalse(payrollId)
                .orElseThrow(() ->new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404));

        PayrollRequest request = new PayrollRequest(
                payroll.getId(), payroll.getMonth().getMonthValue(),
                payroll.getMonth().getYear());
        var cycle = payrollCycleService.getActive();
        var period = calculatePeriod(cycle, request.getYear(), request.getMonth());

        var employee = getEmployee(payroll.getEmployee().getId());
        var salaryContract = getSalaryContract(employee);
        var attendanceList = getAttendanceList(employee, period);
        var salaryAdjustments = getSalaryAdjustments(employee, period);
        var allowance = getAllowance(employee);
        var payrollDetail = calculatePayrollDetail(salaryContract, attendanceList, salaryAdjustments, allowance, cycle);

        return buildPayrollResponse(request, employee, attendanceList, salaryAdjustments, payrollDetail, payroll.getId(), payroll.getStatus());
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public PayrollResponse getDetailByEmployee(String employeeId, int month, int year){
        Payroll payroll = payrollRepository.findByEmployeeIdAndMonthAndIsDeletedFalse(
                employeeId, YearMonth.of(year, month)).orElseThrow(() ->new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404));

        PayrollRequest request = new PayrollRequest(employeeId, month, year);
        var cycle = payrollCycleService.getActive();
        var period = calculatePeriod(cycle, request.getYear(), request.getMonth());

        var employee = getEmployee(employeeId);
        var salaryContract = getSalaryContract(employee);
        var attendanceList = getAttendanceList(employee, period);
        var salaryAdjustments = getSalaryAdjustments(employee, period);
        var allowance = getAllowance(employee);
        var payrollDetail = calculatePayrollDetail(salaryContract, attendanceList, salaryAdjustments, allowance, cycle);

        return buildPayrollResponse(request, employee, attendanceList, salaryAdjustments, payrollDetail, payroll.getId(), payroll.getStatus());
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public ApprovedPayrollListResponse salaryApproval(PayrollApprovalRequest request) throws JsonProcessingException {

        UserAccount user = userAccountRepository.findByUsernameAndIsDeletedFalse(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));


        String monthStr = String.format("%04d-%02d", request.getYear(), request.getMonth());

        List<Payroll> payrollList = payrollRepository
                .findAllByMonthAndStatusAndIsDeletedFalse(YearMonth.of(request.getYear(), request.getMonth()), PayrollStatus.DRAFT);

        if (payrollList.isEmpty()) {
            throw new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404,
                    "không tìm thấy bảng lương nào chưa duyệt trong kỳ"  + monthStr);
        }

        BigDecimal totalPayrollAmount = BigDecimal.ZERO;

        PayrollStatus newStatus = request.getStatus(); // nếu DTO dùng enum

        for (Payroll item : payrollList) {
            item.setStatus(newStatus);
            totalPayrollAmount = totalPayrollAmount.add(item.getTotalSalary());
        }

        payrollRepository.saveAll(payrollList);

        // Lưu lịch sử duyệt

        List<PayrollListItemResponse> dtoList =
                payrollList.stream()
                        .map(payrollResponseMapper::toListResponse)
                        .toList();

        PayrollApprovalHistory history = PayrollApprovalHistory.builder()
                .month(request.getMonth())
                .year(request.getYear())
                .totalAmount(totalPayrollAmount.doubleValue())
                .status(newStatus == PayrollStatus.APPROVED
                        ? PayrollApprovalStatus.APPROVED
                        : PayrollApprovalStatus.REJECTED)
                .comment(request.getComment())
                .approvedBy(user.getEmployee()) // hoặc từ context
                .payrollSnapshot(objectMapper.writeValueAsString(dtoList))
                .build();

        payrollApprovalHistoryRepository.save(history);

        return new ApprovedPayrollListResponse(
                monthStr,
                totalPayrollAmount,
                payrollList.stream()
                        .map(payrollResponseMapper::toListResponse)
                        .toList()
        );
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public PayrollListResponse getPayrollList(PayrollApprovalRequest request){

        String monthStr = String.format("%04d-%02d", request.getYear(), request.getMonth());
        List<Payroll> payrollList = payrollRepository
                .findAllByMonthAndStatusAndIsDeletedFalse(YearMonth.of(request.getYear(), request.getMonth()), request.getStatus());

        if (payrollList.isEmpty()) {
            throw new AppException(ErrorCode.PAYROLL_NOT_FOUND, 404,
                    "Không tìm thấy bảng lương với trạng thái "
                            + request.getStatus() + " trong kỳ " + monthStr);
        }

        BigDecimal totalPayrollAmount = getTotalPayrollAmountByStatus(request);

//        PayrollStatus newStatus = request.getStatus(); // nếu DTO dùng enum

        return new PayrollListResponse(
                monthStr,
                totalPayrollAmount,
                payrollList.stream()
                        .map(payrollResponseMapper::toListResponse)
                        .toList()
        );
    }

    private PayrollPeriodCalculator.PayrollPeriod calculatePeriod(PayrollCycleResponse cycle, int year, int month) {
        return new PayrollPeriodCalculator().calculate(cycle, year, month);
    }

    private Employee getEmployee(String employeeId) {
        log.warn("employeeid: {}", employeeId);
        return employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));
    }

    private void checkPayrollExists(String employeeId, int year, int month) {
        if (payrollRepository.existsByEmployeeIdAndMonthAndIsDeletedFalse(employeeId, YearMonth.of(year, month))) {
            throw new AppException(ErrorCode.PAYROLL_ALREADY_EXISTS, 400);
        }
    }

    private SalaryContract getSalaryContract(Employee employee) {
        // Kiểm tra employee có contracts không

        if ("admin".equalsIgnoreCase(employee.getCode())) {
            return SalaryContract.builder()
                    .baseSalary(BigDecimal.ZERO)
                    .build();
        }

        if (employee.getContracts() == null || employee.getContracts().isEmpty()) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404,
                    employee.getCode() + " Nhân viên chưa có hợp đồng nào");
        }

        var contract = employee.getContracts().getFirst();
        if (contract == null) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404,
                    "Hợp đồng không hợp lệ");
        }

        return salaryContractRepository
                .findByEmployee_IdAndContract_IdAndIsDeletedFalse(
                        employee.getId(),
                        contract.getId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.SALARY_CONTRACT_NOT_FOUND, 404));
    }

    private List<Attendance> getAttendanceList(Employee employee, PayrollPeriodCalculator.PayrollPeriod period) {
        return attendanceRepository.findOTForEmployee(
                employee.getId(),
                period.startDate(),
                period.endDate()
        );
    }

    private List<SalaryAdjustment> getSalaryAdjustments(Employee employee, PayrollPeriodCalculator.PayrollPeriod period) {
        return salaryAdjustmentService.getForPayroll(
                employee.getId(),
                period.startDate(),
                period.endDate()
        );
    }

    private List<AllowanceRule> getAllowance(Employee employee) {
        return allowanceRuleRepository.findActiveRules(
                employee.getPosition().getId(),
                employee.getSubDepartment().getId()
        );
    }

    private PayrollDetailResponse calculatePayrollDetail(SalaryContract salaryContract, List<Attendance> attendanceList, List<SalaryAdjustment> salaryAdjustments, List<AllowanceRule> allowance, PayrollCycleResponse cycle) {
        return payrollCalculator.calculatePayrollDetail(
                salaryContract.getBaseSalary(),
                attendanceList,
                salaryAdjustments,
                allowance,
                cycle
        );
    }

    public BigDecimal getTotalPayrollAmountByStatus(PayrollApprovalRequest request) {
        String monthStr = String.format("%04d-%02d", request.getYear(), request.getMonth());

        return payrollRepository
                .sumTotalSalaryByMonthAndStatusAndIsDeletedFalse(monthStr, request.getStatus())
                .orElse(BigDecimal.ZERO);
    }

    private Payroll buildAndSavePayroll(Employee employee, PayrollRequest request, PayrollDetailResponse payrollDetail) {
        Payroll payroll = Payroll.builder()
                .employee(employee)
                .month(YearMonth.of(request.getYear(), request.getMonth()))
                .baseSalary(payrollDetail.baseSalaryTotal())
                .allowance(payrollDetail.totalAllowance())
                .overtime(payrollDetail.otTotal().doubleValue())
                .bonus(payrollDetail.totalBonus())
                .penalty(payrollDetail.totalPenalty())
                .totalSalary(payrollDetail.totalSalary())
                .status(PayrollStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return payrollRepository.save(payroll);
    }

    private PayrollResponse buildPayrollResponse(
            PayrollRequest request,
            Employee employee,
//            SalaryContract salaryContract,
            List<Attendance> attendanceList,
            List<SalaryAdjustment> salaryAdjustments,
//            List<AllowanceRule> allowance,
//            PayrollCycleResponse cycle,
            PayrollDetailResponse payrollDetail,
            String payrollId,
            PayrollStatus status) {
        return PayrollResponse.builder()
                .payrollId(payrollId)
                .period(payrollResponseMapper.toPeriodResponse(request))
                .employee(payrollResponseMapper.toEmployeeResponse(employee))
                .attendanceSummary(
                        payrollResponseMapper.toAttendanceSummary(attendanceList, payrollDetail)
                )
                .earnings(
                        payrollResponseMapper.toEarningsResponse(
                              payrollDetail, salaryAdjustments
                        )
                )
                .deductions(
                        payrollResponseMapper.toDeductionsResponse(
                                salaryAdjustments, payrollDetail
                        )
                )
                .summary(
                        payrollResponseMapper.toPayrollSummary(
                                payrollResponseMapper.toEarningsResponse(
                                       payrollDetail, salaryAdjustments
                                ),
                                payrollResponseMapper.toDeductionsResponse(
                                        salaryAdjustments, payrollDetail
                                )
                        )
                )
                .metadata(payrollResponseMapper.toMetadata())
                .status(status)
                .build();
    }
}
