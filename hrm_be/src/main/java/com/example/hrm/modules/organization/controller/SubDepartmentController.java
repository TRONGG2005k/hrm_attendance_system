package com.example.hrm.modules.organization.controller;

import com.example.hrm.modules.organization.dto.request.SubDepartmentRequest;
import com.example.hrm.modules.organization.dto.response.SubDepartmentResponse;
import com.example.hrm.modules.organization.dto.response.SubDepartmentResponseDetail;
import com.example.hrm.modules.organization.excel.SubDepartmentExcelService;
import com.example.hrm.modules.organization.service.SubDepartmentService;
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
@RequestMapping("${app.api-prefix}/sub-departments")
@RequiredArgsConstructor
public class SubDepartmentController {

    private final SubDepartmentService subDepartmentService;
    private final SubDepartmentExcelService subDepartmentExcelService;

    @PostMapping
    public SubDepartmentResponse create(@RequestBody SubDepartmentRequest request) {
        return subDepartmentService.createSubDepartment(request);
    }

    @GetMapping
    public Page<SubDepartmentResponse> getAll(Pageable pageable) {
        return subDepartmentService.getAllSubDepartments(pageable);
    }

    @GetMapping("/department/{departmentId}")
    public Page<SubDepartmentResponse> getByDepartment(@PathVariable String departmentId, Pageable pageable) {
        return subDepartmentService.getSubDepartmentsByDepartment(departmentId, pageable);
    }

    @GetMapping("/{id}")
    public SubDepartmentResponseDetail getById(@PathVariable String id) {
        return subDepartmentService.getSubDepartmentById(id);
    }

    @PutMapping("/{id}")
    public SubDepartmentResponse update(@PathVariable String id, @RequestBody SubDepartmentRequest request) {
        return subDepartmentService.updateSubDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        subDepartmentService.deleteSubDepartment(id);
    }

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ExcelResult> importSubDepartments(@RequestParam("file") MultipartFile file) {
        ExcelResult result = subDepartmentExcelService.importFile(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportSubDepartments() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        subDepartmentExcelService.exportFile(outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "sub-departments.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }
}
