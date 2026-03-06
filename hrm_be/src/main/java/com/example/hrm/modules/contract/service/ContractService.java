package com.example.hrm.modules.contract.service;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.ContractUpdateRequest;
import com.example.hrm.modules.contract.dto.response.ContractListResponse;
import com.example.hrm.modules.contract.dto.response.ContractResponse;
import com.example.hrm.modules.contract.entity.Contract;
import com.example.hrm.modules.contract.entity.ContractAllowance;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.modules.contract.excel.dto.ContractExcelDto;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.contract.repository.ContractRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.file.mapper.FileAttachmentMapper;
import com.example.hrm.modules.file.repository.FileAttachmentRepository;
import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.shared.enums.RefType;
import com.example.hrm.shared.enums.SalaryContractStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.contract.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryContractRepository salaryContractRepository;
    private final AllowanceRuleRepository allowanceRuleRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final ContractMapper contractMapper;

    /* ========================= CREATE ========================= */

    /**
     * Tạo Contract (pháp lý) + SalaryContract (snapshot lương)
     */
    @Transactional
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ContractResponse create(ContractRequest request) {

        // 1. Lấy employee
        var employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));
        if (employee.getPosition() == null || employee.getSubDepartment() == null) {
            throw new AppException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    400,
                    "Employee must have position and department before creating contract"
            );
        }
        // 2. Tạo Contract (pháp lý)
        Contract contract = contractMapper.toEntity(request);
        contract.setEmployee(employee);
        contractRepository.save(contract);

        // 3. Deactivate SalaryContract ACTIVE cũ (nếu có) - Lấy danh sách và deactivate tất cả
        var existingContracts = salaryContractRepository.findAllByEmployeeIdAndIsDeletedFalse(employee.getId());
        for (SalaryContract old : existingContracts) {
            if (old.getStatus() == SalaryContractStatus.ACTIVE) {
                old.setStatus(SalaryContractStatus.INACTIVE);
                salaryContractRepository.save(old);
            }
        }

        // 4. Lấy allowance rule theo employee
        var rules = allowanceRuleRepository.findActiveRules(
                employee.getPosition().getId(),
                employee.getSubDepartment().getId()
        );

        var salaryReq = request.getSalaryContract();

        // 5. Tạo SalaryContract (snapshot)
        SalaryContract salaryContract = SalaryContract.builder()
                .employee(employee)
                .contract(contract)
                .baseSalary(salaryReq.getBaseSalary())
                .salaryCoefficient(salaryReq.getSalaryCoefficient())
                .effectiveDate(salaryReq.getEffectiveDate())
                .status(SalaryContractStatus.ACTIVE)
                .build();

        // 6. Snapshot allowance → ContractAllowance
        var contractAllowances = rules.stream()
                .map(rule -> ContractAllowance.builder()
                        .contract(salaryContract)
                        .allowance(rule.getAllowance())
                        .amount(rule.getAmount())
                        .calculationType(rule.getCalculationType())
                        .effectiveFrom(salaryContract.getEffectiveDate())
                        .build()
                )
                .toList();

        salaryContract.setAllowances(contractAllowances);
        salaryContractRepository.save(salaryContract);

        return buildResponse(contract);
    }

    /* ========================= UPDATE ========================= */

    /**
     * Update Contract + tạo SalaryContract mới nếu có thay đổi lương
     */
    @Transactional
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ContractResponse update(String id, ContractUpdateRequest request) {

        Contract contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        // 1. Update Contract (pháp lý)
        contractMapper.ContractUpdateRequestToEntity(request, contract);
        contractRepository.save(contract);

        var salaryReq = request.getSalaryContract();

        // 2. Không update lương → chỉ update contract
        if (salaryReq == null) {
            return buildResponse(contract);
        }

        // 3. Deactivate SalaryContract ACTIVE cũ
        salaryContractRepository
                .findActiveByEmployee_IdAndContract_Id(
                        contract.getEmployee().getId(),
                        contract.getId()
                )
                .ifPresent(old -> {
                    old.setStatus(SalaryContractStatus.INACTIVE);
                    salaryContractRepository.save(old);
                });

        // 4. Lấy rule mới theo employee hiện tại
        var employee = contract.getEmployee();
        var rules = allowanceRuleRepository.findActiveRules(
                employee.getPosition().getId(),
                employee.getSubDepartment().getId()
        );

        // 5. Tạo SalaryContract MỚI
        SalaryContract newSalaryContract = SalaryContract.builder()
                .employee(employee)
                .contract(contract)
                .baseSalary(salaryReq.getBaseSalary())
                .salaryCoefficient(salaryReq.getSalaryCoefficient())
                .effectiveDate(salaryReq.getEffectiveDate())
                .status(SalaryContractStatus.ACTIVE)
                .build();

        // 6. Snapshot allowance mới
        var contractAllowances = rules.stream()
                .map(rule -> ContractAllowance.builder()
                        .contract(newSalaryContract)
                        .allowance(rule.getAllowance())
                        .amount(rule.getAmount())
                        .calculationType(rule.getCalculationType())
                        .effectiveFrom(newSalaryContract.getEffectiveDate())
                        .build()
                )
                .toList();

        newSalaryContract.setAllowances(contractAllowances);
        salaryContractRepository.save(newSalaryContract);

        return buildResponse(contract);
    }

    /* ========================= QUERY ========================= */

    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ContractResponse getById(String id) {

        Contract contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        return buildResponse(contract);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<ContractListResponse> getAllContractActive(int page, int size) {
        return contractRepository
                .findByIsDeletedFalseAndStatus(PageRequest.of(page, size), ContractStatus.ACTIVE)
                .map(c -> {

                    var response = contractMapper.toListResponse(c);
                    response.setEmployeeCode(c.getEmployee().getCode());
                    response.setEmployeeName(c.getEmployee().getFirstName() + " " + c.getEmployee().getLastName());
                    response.setContractCode(c.getCode());
                    response.setContractType(c.getType().name());
                    response.setEmployeeId(c.getEmployee().getId());
                    response.setBaseSalary(c.getSalaryContracts().getFirst().getBaseSalary());
                    return response;
                });
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public Page<ContractListResponse> getAllContractNotActive(int page, int size) {
        return contractRepository
                .findByIsDeletedFalseAndStatusNot(PageRequest.of(page, size), ContractStatus.ACTIVE)
                .map(contractMapper::toListResponse);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public ContractResponse changeContractStatus(String id, ContractStatus status) {

        Contract contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        contract.setStatus(status);
        contractRepository.save(contract);

        return buildResponse(contract);
    }

    /* ========================= PRIVATE ========================= */

    private ContractResponse buildResponse(Contract contract) {

        var files = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(
                        RefType.CONTRACT.getValue(),
                        contract.getId()
                );

        var response = contractMapper.toResponse(contract);
        response.setFileAttachmentResponses(files.stream().map(fileAttachmentMapper::toResponse).toList());

        return response;
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public List<ContractExcelDto> getAllForExcel() {
        return contractRepository.findAllForExcel();
    }

}
