package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.WardRequest;
import com.example.hrm.modules.employee.dto.response.WardResponse;
import com.example.hrm.modules.employee.service.WardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/wards")
@RequiredArgsConstructor
public class WardController {

    private final WardService wardService;

    @PostMapping
    public ResponseEntity<WardResponse> create(@Valid @RequestBody WardRequest request) {
        WardResponse response = wardService.createWard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<WardResponse>> getAll(Pageable pageable) {
        Page<WardResponse> response = wardService.getAllWards(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<Page<WardResponse>> getByDistrict(@PathVariable String districtId, Pageable pageable) {
        Page<WardResponse> response = wardService.getWardsByDistrict(districtId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WardResponse> getById(@PathVariable String id) {
        WardResponse response = wardService.getWardById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WardResponse> update(@PathVariable String id, @Valid @RequestBody WardRequest request) {
        WardResponse response = wardService.updateWard(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        wardService.deleteWard(id);
        return ResponseEntity.noContent().build();
    }
}

