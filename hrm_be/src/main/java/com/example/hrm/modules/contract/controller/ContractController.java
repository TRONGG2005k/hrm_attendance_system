package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.ContractUpdateRequest;
import com.example.hrm.modules.contract.dto.response.ContractListResponse;
import com.example.hrm.modules.contract.dto.response.ContractResponse;
import com.example.hrm.modules.contract.excel.ContractExcelService;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.modules.contract.service.ContractService;
import com.example.hrm.modules.contract.service.ContractUploadService;
import com.example.hrm.shared.BulkUploadResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api-prefix}/contract")
public class ContractController {
    private final ContractService contractService;
    private final ContractExcelService contractExcelService;
    private final ContractUploadService contractUploadService;

    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ContractUpdateRequest request
    ) {
        return ResponseEntity.ok(contractService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<ContractListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(contractService.getAllContractActive(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(contractService.getById(id));
    }

    @GetMapping("/not-active")
    public Page<ContractListResponse> getContractsNotActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return contractService.getAllContractNotActive(page, size);
    }


    @PostMapping("/{contractId}/approve")
    public ContractResponse approveContract(
            @PathVariable String contractId,
            @RequestParam ContractStatus newStatus
    ) {
        return contractService.changeContractStatus(contractId, newStatus);
    }

    @PostMapping("/import")
    public ResponseEntity<ExcelResult> importContract(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(contractExcelService.importFile(file));
    }

    @PostMapping("/upload-files")
    public ResponseEntity<BulkUploadResult> uploadContractFiles(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(contractUploadService.importFile(file));
    }

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportContract() {
        String filename = "contracts.xlsx";
        ByteArrayResource file = contractExcelService.exportFile();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
}
