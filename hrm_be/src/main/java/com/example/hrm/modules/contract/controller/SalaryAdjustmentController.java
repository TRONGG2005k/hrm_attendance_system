package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.SalaryAdjustmentRequest;
import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.modules.contract.entity.SalaryAdjustment;
import com.example.hrm.modules.contract.service.SalaryAdjustmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/salary-adjustments")
@RequiredArgsConstructor
public class SalaryAdjustmentController {

    private final SalaryAdjustmentService adjustmentService;

    /* ================= CREATE ================= */

    @PostMapping
    public SalaryAdjustmentResponse create(
            @Valid @RequestBody SalaryAdjustmentRequest request
    ) {
        return adjustmentService.create(request);
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    public SalaryAdjustmentResponse update(
            @PathVariable String id,
            @Valid @RequestBody SalaryAdjustmentRequest request
    ) {
        return adjustmentService.update(id, request);
    }

    /* ================= DELETE (SOFT) ================= */

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        adjustmentService.delete(id);
    }

    /* ================= QUERY FOR UI ================= */

    @GetMapping("/employee/{employeeId}")
    public Page<SalaryAdjustmentResponse> getAllByEmployee(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return adjustmentService.getAllByEmployee(employeeId, pageable);
    }

    /* ================= QUERY FOR PAYROLL ================= */
    /**
     * API này dùng cho PayrollService
     * Lấy toàn bộ thưởng / phạt trong kỳ lương
     */
    @GetMapping("/employee/{employeeId}/range")
    public List<SalaryAdjustment> getForPayroll(
            @PathVariable String employeeId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return adjustmentService.getForPayroll(employeeId, startDate, endDate);
    }
}
