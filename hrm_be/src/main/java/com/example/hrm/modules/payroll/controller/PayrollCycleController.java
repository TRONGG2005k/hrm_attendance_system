package com.example.hrm.modules.payroll.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hrm.modules.payroll.dto.request.PayrollCycleRequest;
import com.example.hrm.modules.payroll.dto.response.PayrollCycleResponse;
import com.example.hrm.modules.payroll.service.PayrollCycleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${app.api-prefix}payroll-cycles")
@RequiredArgsConstructor
public class PayrollCycleController {

    private final PayrollCycleService payrollCycleService;

    /**
     * Tạo chu kỳ lương mới
     */
    @PostMapping
    public ResponseEntity<PayrollCycleResponse> create(
        @RequestBody @Valid PayrollCycleRequest request,
        String createrId
    ) {
        PayrollCycleResponse response =
            payrollCycleService.create(request, createrId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    /**
     * Lấy chu kỳ lương đang active
     */
    @GetMapping("/active")
    public ResponseEntity<PayrollCycleResponse> getActive() {
        return ResponseEntity.ok(
            payrollCycleService.getActive()
        );
    }

    /**
     * Lấy lịch sử thay đổi chu kỳ lương
     */
    @GetMapping("/history")
    public ResponseEntity<List<PayrollCycleResponse>> getHistory() {
        return ResponseEntity.ok(
            payrollCycleService.getHistory()
        );
    }
}
