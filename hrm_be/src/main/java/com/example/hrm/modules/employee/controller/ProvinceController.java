package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.ProvinceRequest;
import com.example.hrm.modules.employee.dto.response.ProvinceResponse;
import com.example.hrm.modules.employee.service.ProvinceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @PostMapping
    public ResponseEntity<ProvinceResponse> create(@Valid @RequestBody ProvinceRequest request) {
        ProvinceResponse response = provinceService.createProvince(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProvinceResponse>> getAll(Pageable pageable) {
        Page<ProvinceResponse> response = provinceService.getAllProvinces(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinceResponse> getById(@PathVariable String id) {
        ProvinceResponse response = provinceService.getProvinceById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProvinceResponse> update(@PathVariable String id, @Valid @RequestBody ProvinceRequest request) {
        ProvinceResponse response = provinceService.updateProvince(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        provinceService.deleteProvince(id);
        return ResponseEntity.noContent().build();
    }
}

