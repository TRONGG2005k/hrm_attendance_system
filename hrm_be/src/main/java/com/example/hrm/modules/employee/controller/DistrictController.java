package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.DistrictRequest;
import com.example.hrm.modules.employee.dto.response.DistrictResponse;
import com.example.hrm.modules.employee.service.DistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @PostMapping
    public ResponseEntity<DistrictResponse> create(@Valid @RequestBody DistrictRequest request) {
        DistrictResponse response = districtService.createDistrict(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DistrictResponse>> getAll(Pageable pageable) {
        Page<DistrictResponse> response = districtService.getAllDistricts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/province/{provinceId}")
    public ResponseEntity<Page<DistrictResponse>> getByProvince(@PathVariable String provinceId, Pageable pageable) {
        Page<DistrictResponse> response = districtService.getDistrictsByProvince(provinceId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictResponse> getById(@PathVariable String id) {
        DistrictResponse response = districtService.getDistrictById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistrictResponse> update(@PathVariable String id, @Valid @RequestBody DistrictRequest request) {
        DistrictResponse response = districtService.updateDistrict(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        districtService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }
}

