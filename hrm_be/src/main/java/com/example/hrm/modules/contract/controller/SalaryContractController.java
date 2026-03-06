package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.SalaryContractRequest;
import com.example.hrm.modules.contract.dto.response.SalaryContractResponse;
import com.example.hrm.modules.contract.service.SalaryContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/salary-contract")
@RequiredArgsConstructor
public class SalaryContractController {

    private final SalaryContractService contractService;

    @PostMapping
    public SalaryContractResponse create(@RequestBody SalaryContractRequest request) {
        return contractService.create(request);
    }

    @GetMapping("/employee/{employeeId}")
    public Page<SalaryContractResponse> getAllByEmployee(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return contractService.getAllByEmployee(employeeId, pageable);
    }

    @PutMapping("/{id}")
    public SalaryContractResponse update(@PathVariable String id, @RequestBody SalaryContractRequest request) {
        return contractService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        contractService.delete(id);
    }
}
