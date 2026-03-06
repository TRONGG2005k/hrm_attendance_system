package com.example.hrm.modules.organization.controller;

import com.example.hrm.modules.organization.dto.request.DepartmentRequest;
import com.example.hrm.modules.organization.dto.response.DepartmentResponse;
import com.example.hrm.modules.organization.service.DepartmentService;
import com.example.hrm.modules.organization.excel.DepartmentExcelService;
import com.example.hrm.shared.ExcelResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@RestController
@RequestMapping("${app.api-prefix}/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentExcelService departmentExcelService;

    @PostMapping
    public DepartmentResponse create(@RequestBody DepartmentRequest request) {
        return departmentService.createDepartment(request);
    }

    @GetMapping
    public Page<DepartmentResponse> getAll(Pageable pageable) {
        return departmentService.getAllDepartments(pageable);
    }

    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable String id) {
        return departmentService.getDepartmentById(id);
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable String id, @RequestBody DepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        departmentService.deleteDepartment(id);
    }

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ExcelResult> importDepartments(@RequestParam("file") MultipartFile file) {
        ExcelResult result = departmentExcelService.importFile(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDepartments() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        departmentExcelService.exportFile(outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "departments.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
}
