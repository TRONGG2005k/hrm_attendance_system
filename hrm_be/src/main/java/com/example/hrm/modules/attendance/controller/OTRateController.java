package com.example.hrm.modules.attendance.controller;

import com.example.hrm.modules.attendance.dto.request.OTRateRequest;
import com.example.hrm.modules.attendance.dto.response.OTRateResponse;
import com.example.hrm.shared.enums.OTType;
import com.example.hrm.modules.attendance.service.OTRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/ot-rates")
@RequiredArgsConstructor
public class OTRateController {

    private final OTRateService otRateService;

    @PostMapping
    public ResponseEntity<OTRateResponse> create(@Valid @RequestBody OTRateRequest request) {
        return ResponseEntity.ok(otRateService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OTRateResponse> update(
            @PathVariable String id,
            @Valid @RequestBody OTRateRequest request
    ) {
        return ResponseEntity.ok(otRateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        otRateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OTRateResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(otRateService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OTRateResponse>> getAll() {
        return ResponseEntity.ok(otRateService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<OTRateResponse> getByDateAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam OTType type
    ) {
        return ResponseEntity.ok(otRateService.getByDateAndType(date, type));
    }
}
