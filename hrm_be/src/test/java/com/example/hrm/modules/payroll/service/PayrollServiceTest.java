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
import com.example.hrm.modules.payroll.dto.response.PayrollCycleResponse;
import com.example.hrm.modules.payroll.dto.response.PayrollDetailResponse;
import com.example.hrm.modules.payroll.dto.response.PayrollResponse;
import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.modules.payroll.mapper.PayrollResponseMapper;
import com.example.hrm.modules.payroll.repository.PayrollApprovalHistoryRepository;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.enums.PayrollApprovalStatus;
import com.example.hrm.shared.enums.PayrollStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;
    @Mock
    private SalaryContractRepository salaryContractRepository;
    @Mock
    private SalaryAdjustmentService salaryAdjustmentService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private PayrollCycleService payrollCycleService;
    @Mock
    private PayrollCalculator payrollCalculator;
    @Mock
    private AllowanceRuleRepository allowanceRuleRepository;
    @Mock
    private PayrollResponseMapper payrollResponseMapper;
    @Mock
    private PayrollApprovalHistoryRepository payrollApprovalHistoryRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PayrollService payrollService;

    private PayrollRequest payrollRequest;
    private Employee employee;
    private SalaryContract salaryContract;
    private PayrollCycleResponse payrollCycleResponse;
    private PayrollPeriodCalculator.PayrollPeriod payrollPeriod;
    private List<Attendance> attendanceList;
    private List<SalaryAdjustment> salaryAdjustments;
    private List<AllowanceRule> allowanceRules;
    private PayrollDetailResponse payrollDetailResponse;

    @BeforeEach
    void setUp() {
        payrollRequest = new PayrollRequest("emp123", 1, 2026);

        employee = new Employee();
        employee.setId("emp123");
        employee.setContracts(List.of(new com.example.hrm.modules.contract.entity.Contract() {{ setId("contract1"); }})); // Mock contract later
        employee.setPosition(new com.example.hrm.modules.organization.entity.Position() {{ setId("pos123"); }});
        employee.setSubDepartment(new com.example.hrm.modules.organization.entity.SubDepartment() {{ setId("subdep123"); }});

        salaryContract = SalaryContract.builder()
                .baseSalary(new BigDecimal("10000.00"))
                .build();

        payrollCycleResponse = PayrollCycleResponse.builder()
                .id(1L)
                .startDay(1)
                .endDay(15)
                .payday(20)
                .workingDays(22)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        payrollPeriod = new PayrollPeriodCalculator.PayrollPeriod(LocalDate.of(2025, 12, 16), LocalDate.of(2026, 1, 15));

        attendanceList = Collections.emptyList();
        salaryAdjustments = Collections.emptyList();
        allowanceRules = Collections.emptyList();

        payrollDetailResponse = new PayrollDetailResponse(
                new BigDecimal("10000.00"), // totalSalary
                new BigDecimal("10000.00"), // baseSalaryTotal
                BigDecimal.ZERO, // otTotal
                Collections.emptyMap(), // allowance
                BigDecimal.ZERO, // totalAllowance
                BigDecimal.ZERO, // totalBonus
                BigDecimal.ZERO, // totalPenalty
                BigDecimal.ZERO, // fullAttendanceBonus
                22L // workingDays
        );
    }

    @Test
    @DisplayName("Should create payroll successfully")
    void createPayroll_Success() {
        when(payrollCycleService.getActive()).thenReturn(payrollCycleResponse);
        when(employeeRepository.findByIdAndIsDeletedFalse(anyString())).thenReturn(Optional.of(employee));
        when(payrollRepository.existsByEmployeeIdAndMonthAndIsDeletedFalse(anyString(), any(YearMonth.class))).thenReturn(false);
        when(salaryContractRepository.findByEmployee_IdAndContract_IdAndIsDeletedFalse(anyString(), anyString())).thenReturn(Optional.of(salaryContract));
        when(attendanceRepository.findOTForEmployee(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(attendanceList);
        when(salaryAdjustmentService.getForPayroll(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(salaryAdjustments);
        when(allowanceRuleRepository.findActiveRules(anyString(), anyString())).thenReturn(allowanceRules);
        when(payrollCalculator.calculatePayrollDetail(any(BigDecimal.class), anyList(), anyList(), anyList(), any(PayrollCycleResponse.class))).thenReturn(payrollDetailResponse);
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> {
            Payroll payroll = invocation.getArgument(0);
            payroll.setId("payroll123"); // Simulate ID generation
            return payroll;
        });

        // Mock buildPayrollResponse internal calls
        PayrollResponse mockPayrollResponse = PayrollResponse.builder().payrollId("payroll123").status(PayrollStatus.DRAFT).build();
        when(payrollResponseMapper.toPeriodResponse(any())).thenReturn(null);
        when(payrollResponseMapper.toEmployeeResponse(any())).thenReturn(null);
        when(payrollResponseMapper.toAttendanceSummary(anyList(), any())).thenReturn(null);
        when(payrollResponseMapper.toEarningsResponse(any(), anyList())).thenReturn(null);
        when(payrollResponseMapper.toDeductionsResponse(anyList(), any())).thenReturn(null);
        when(payrollResponseMapper.toPayrollSummary(any(), any())).thenReturn(null);
        when(payrollResponseMapper.toMetadata()).thenReturn(null);


        PayrollResponse result = payrollService.create(payrollRequest);

        assertNotNull(result);
        assertEquals("payroll123", result.getPayrollId());
        assertEquals(PayrollStatus.DRAFT, result.getStatus());

        verify(payrollRepository, times(1)).save(any(Payroll.class));
    }

    @Test
    @DisplayName("Should throw AppException when employee not found during payroll creation")
    void createPayroll_EmployeeNotFound_ThrowsAppException() {
        when(payrollCycleService.getActive()).thenReturn(payrollCycleResponse);
        when(employeeRepository.findByIdAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> payrollService.create(payrollRequest));
        assertEquals(ErrorCode.EMPLOYEE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw AppException when payroll already exists for employee and month")
    void createPayroll_PayrollAlreadyExists_ThrowsAppException() {
        when(payrollCycleService.getActive()).thenReturn(payrollCycleResponse);
        when(employeeRepository.findByIdAndIsDeletedFalse(anyString())).thenReturn(Optional.of(employee));
        when(payrollRepository.existsByEmployeeIdAndMonthAndIsDeletedFalse(anyString(), any(YearMonth.class))).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> payrollService.create(payrollRequest));
        assertEquals(ErrorCode.PAYROLL_ALREADY_EXISTS, exception.getErrorCode());
    }

    // Additional test cases for other methods like createForAllEmployees, salaryApproval, etc.
    // ...

    @Test
    @DisplayName("Should approve payrolls successfully")
    void salaryApproval_Success() throws Exception {
        // Mock SecurityContextHolder
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        // Mock UserAccount and Employee
        com.example.hrm.modules.user.entity.UserAccount userAccount = new com.example.hrm.modules.user.entity.UserAccount();
        userAccount.setEmployee(new Employee()); // Assuming Employee is set here
        when(userAccountRepository.findByUsernameAndIsDeletedFalse(anyString())).thenReturn(Optional.of(userAccount));

        // Mock PayrollApprovalRequest
        PayrollApprovalRequest approvalRequest = new PayrollApprovalRequest();
        approvalRequest.setYear(2026);
        approvalRequest.setMonth(1);
        approvalRequest.setStatus(PayrollStatus.APPROVED);
        approvalRequest.setComment("Approved for January");

        // Mock Payroll list
        Payroll payroll1 = Payroll.builder()
                .id("p1")
                .totalSalary(new BigDecimal("5000.00"))
                .status(PayrollStatus.DRAFT)
                .month(YearMonth.of(2026,1))
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        Payroll payroll2 = Payroll.builder()
                .id("p2")
                .totalSalary(new BigDecimal("7000.00"))
                .status(PayrollStatus.DRAFT)
                .month(YearMonth.of(2026,1))
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<Payroll> payrollList = List.of(payroll1, payroll2);

        when(payrollRepository.findAllByMonthAndStatusAndIsDeletedFalse(any(YearMonth.class), eq(PayrollStatus.DRAFT)))
                .thenReturn(payrollList);
        when(payrollRepository.saveAll(anyList())).thenReturn(payrollList);
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[{\"id\":\"p1\"}, {\"id\":\"p2\"}]"); // Simplified mock

        // Mock PayrollResponseMapper
        when(payrollResponseMapper.toListResponse(any(Payroll.class)))
                .thenReturn(new com.example.hrm.modules.payroll.dto.response.PayrollListItemResponse(
                        "p1", "emp1", "EMP001", "Test Employee", 
                        new com.example.hrm.modules.payroll.dto.response.PeriodResponse(1, 2026), 
                        new BigDecimal("5000.00"), new BigDecimal("5000.00"), PayrollStatus.APPROVED, LocalDateTime.now())); // Corrected

        payrollService.salaryApproval(approvalRequest);

        verify(payrollRepository, times(1)).saveAll(payrollList);
        verify(payrollApprovalHistoryRepository, times(1)).save(any(com.example.hrm.modules.payroll.entity.PayrollApprovalHistory.class));

        assertEquals(PayrollStatus.APPROVED, payroll1.getStatus());
        assertEquals(PayrollStatus.APPROVED, payroll2.getStatus());
    }

    @Test
    @DisplayName("Should throw AppException when no payrolls found for approval")
    void salaryApproval_NoPayrollsFound_ThrowsAppException() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        com.example.hrm.modules.user.entity.UserAccount userAccount = new com.example.hrm.modules.user.entity.UserAccount();
        userAccount.setEmployee(new Employee());
        when(userAccountRepository.findByUsernameAndIsDeletedFalse(anyString())).thenReturn(Optional.of(userAccount));

        PayrollApprovalRequest approvalRequest = new PayrollApprovalRequest();
        approvalRequest.setYear(2026);
        approvalRequest.setMonth(1);
        approvalRequest.setStatus(PayrollStatus.APPROVED);

        when(payrollRepository.findAllByMonthAndStatusAndIsDeletedFalse(any(YearMonth.class), eq(PayrollStatus.DRAFT)))
                .thenReturn(Collections.emptyList());

        AppException exception = assertThrows(AppException.class, () -> payrollService.salaryApproval(approvalRequest));
        assertEquals(ErrorCode.PAYROLL_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("không tìm thấy bảng lương nào chưa duyệt trong kỳ"));
    }
}
