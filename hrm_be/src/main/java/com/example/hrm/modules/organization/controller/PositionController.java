package com.example.hrm.modules.organization.controller;

import com.example.hrm.modules.organization.dto.request.PositionRequest;
import com.example.hrm.modules.organization.dto.response.PositionResponse;
import com.example.hrm.modules.organization.excel.PositionExcelService;
import com.example.hrm.modules.organization.service.PositionService;
import com.example.hrm.shared.ExcelResult;
// import com.example.hrm.shared.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService service;
    private  final PositionExcelService service1;

    @PostMapping
    public PositionResponse create(@RequestBody @Valid PositionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public PositionResponse update(
            @PathVariable String id,
            @RequestBody @Valid PositionRequest request
    ) {
        return service.update(id, request);
    }

    @GetMapping
    public List<PositionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PositionResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ExcelResult> importFile(@RequestParam("file") MultipartFile file) {
        ExcelResult result = service1.importFile(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportFile() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        service1.exportFile(outputStream);
        
        byte[] fileContent = outputStream.toByteArray();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "positions.xlsx");
        headers.setContentLength(fileContent.length);
        
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
}
