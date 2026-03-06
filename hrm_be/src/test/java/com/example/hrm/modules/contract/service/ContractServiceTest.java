package com.example.hrm.modules.contract.service;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.SalaryContractRequest;
import com.example.hrm.modules.contract.dto.response.ContractResponse;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.contract.entity.Contract;
import com.example.hrm.modules.contract.entity.ContractAllowance;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.modules.contract.mapper.ContractMapper;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.contract.repository.ContractRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.file.mapper.FileAttachmentMapper;
import com.example.hrm.modules.file.repository.FileAttachmentRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private SalaryContractRepository salaryContractRepository;
    @Mock
    private AllowanceRuleRepository allowanceRuleRepository;
    @Mock
    private FileAttachmentRepository fileAttachmentRepository;
    @Mock
    private FileAttachmentMapper fileAttachmentMapper;
    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractService contractService;

    private ContractRequest contractRequest;
    private Employee employee;
    private Contract contract;
    private SalaryContract salaryContract;
    private AllowanceRule allowanceRule;

    @BeforeEach
    void setUp() {
        Position position = new Position();
        position.setId("pos123");
        SubDepartment subDepartment = new SubDepartment();
        subDepartment.setId("subdep123");

        employee = new Employee();
        employee.setId("emp123");
        employee.setPosition(position);
        employee.setSubDepartment(subDepartment);
        employee.setContracts(Collections.emptyList());

        contract = new Contract();
        contract.setId("con123");
        contract.setEmployee(employee);

        salaryContract = SalaryContract.builder()
                .baseSalary(new BigDecimal("10000.00"))
                .effectiveDate(LocalDate.now())
                .build();

        SalaryContractRequest salaryContractRequest = new SalaryContractRequest();
        salaryContractRequest.setBaseSalary(new BigDecimal("10000.00"));
        salaryContractRequest.setSalaryCoefficient(BigDecimal.ONE.doubleValue());
        salaryContractRequest.setEffectiveDate(LocalDate.now());

        contractRequest = new ContractRequest();
        contractRequest.setEmployeeId("emp123");
        contractRequest.setSalaryContract(salaryContractRequest);

        allowanceRule = new AllowanceRule();
        allowanceRule.setAllowance(new com.example.hrm.modules.contract.entity.Allowance());
        allowanceRule.setAmount(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Should create contract successfully")
    void createContract_Success() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(contractMapper.toEntity(any(ContractRequest.class))).thenReturn(contract);
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(salaryContractRepository.findByEmployeeIdAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());
        when(allowanceRuleRepository.findActiveRules(anyString(), anyString())).thenReturn(List.of(allowanceRule));
        when(salaryContractRepository.save(any(SalaryContract.class))).thenReturn(salaryContract);
        when(contractMapper.toResponse(any(Contract.class))).thenReturn(ContractResponse.builder().id("con123").baseSalary(BigDecimal.valueOf(10000.00).doubleValue()).employee(new com.example.hrm.modules.employee.dto.response.EmployeeResponse()).build());
        when(fileAttachmentRepository.findByRefTypeAndRefIdAndIsDeletedFalse(anyString(), anyString())).thenReturn(Collections.emptyList());

        ContractResponse result = contractService.create(contractRequest);

        assertNotNull(result);
        assertEquals("con123", result.getId());

        verify(contractRepository, times(1)).save(any(Contract.class));
        verify(salaryContractRepository, times(1)).save(any(SalaryContract.class));
    }

    @Test
    @DisplayName("Should throw AppException when employee not found during contract creation")
    void createContract_EmployeeNotFound_ThrowsAppException() {
        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> contractService.create(contractRequest));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw AppException when employee has no position or department")
    void createContract_EmployeeMissingPositionOrDepartment_ThrowsAppException() {
        employee.setPosition(null);
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));

        AppException exception = assertThrows(AppException.class, () -> contractService.create(contractRequest));
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Employee must have position and department"));
    }

    @Test
    @DisplayName("Should deactivate old salary contract if exists")
    void createContract_DeactivateOldSalaryContract() {
        SalaryContract oldSalaryContract = SalaryContract.builder()
                .id("oldSalCon1")
                .status(com.example.hrm.shared.enums.SalaryContractStatus.ACTIVE)
                .build();

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(contractMapper.toEntity(any(ContractRequest.class))).thenReturn(contract);
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(salaryContractRepository.findByEmployeeIdAndIsDeletedFalse(anyString())).thenReturn(Optional.of(oldSalaryContract));
        when(allowanceRuleRepository.findActiveRules(anyString(), anyString())).thenReturn(List.of(allowanceRule));
        when(salaryContractRepository.save(any(SalaryContract.class))).thenReturn(salaryContract);
        when(contractMapper.toResponse(any(Contract.class))).thenReturn(ContractResponse.builder().id("con123").baseSalary(BigDecimal.valueOf(10000.00).doubleValue()).employee(new com.example.hrm.modules.employee.dto.response.EmployeeResponse()).build());
        when(fileAttachmentRepository.findByRefTypeAndRefIdAndIsDeletedFalse(anyString(), anyString())).thenReturn(Collections.emptyList());

        contractService.create(contractRequest);

        verify(salaryContractRepository, times(2)).save(any(SalaryContract.class)); // One for old, one for new
        assertEquals(com.example.hrm.shared.enums.SalaryContractStatus.INACTIVE, oldSalaryContract.getStatus());
    }

    // Add more tests for update, getById, getAllContractActive, etc.
}
