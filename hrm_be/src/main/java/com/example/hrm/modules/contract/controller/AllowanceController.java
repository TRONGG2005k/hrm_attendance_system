package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.excel.AllowanceExcelService;
import com.example.hrm.modules.contract.service.AllowanceService;
import com.example.hrm.shared.ExcelResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/allowances")
@RequiredArgsConstructor
public class AllowanceController {

    private final AllowanceService service;
    private final AllowanceExcelService excelService;

    @PostMapping
    public AllowanceResponse create(
            @RequestBody @Valid AllowanceRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<AllowanceResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AllowanceResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AllowanceResponse update(
            @PathVariable String id,
            @RequestBody @Valid AllowanceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/import")
    public ExcelResult importFile(@RequestParam("file") MultipartFile file) {
        return excelService.importFile(file);
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportTemplate() {
        ByteArrayInputStream in = excelService.exportData();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=allowance_template.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
