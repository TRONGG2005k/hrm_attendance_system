package com.example.hrm.modules.contract.service;

import com.example.hrm.modules.contract.dto.request.SalaryContractRequest;
import com.example.hrm.modules.contract.dto.response.SalaryContractResponse;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.modules.contract.mapper.SalaryContractMapper;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalaryContractService {

    private final SalaryContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryContractMapper mapper;

    public SalaryContractResponse create(SalaryContractRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        SalaryContract contract = mapper.toEntity(request);
        contract.setEmployee(employee);

        contractRepository.save(contract);

        return mapper.toResponse(contract);
    }

    public Page<SalaryContractResponse> getAllByEmployee(String employeeId, Pageable pageable) {
        return contractRepository.findByEmployee_IdAndIsDeletedFalse(employeeId, pageable)
                .map(mapper::toResponse);
    }

    public SalaryContractResponse update(String id, SalaryContractRequest request) {
        SalaryContract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        contract.setBaseSalary(request.getBaseSalary());
//        contract.setAllowances(request.getAllowance());
        contract.setSalaryCoefficient(request.getSalaryCoefficient());
        contract.setEffectiveDate(request.getEffectiveDate());
        contract.setStatus(request.getStatus());

        contractRepository.save(contract);

        return mapper.toResponse(contract);
    }

    public void delete(String id) {
        SalaryContract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        contract.setIsDeleted(true);
        contractRepository.save(contract);
    }
}
