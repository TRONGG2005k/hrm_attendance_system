package com.example.hrm.modules.penalty.controller;

import com.example.hrm.modules.penalty.dto.request.PenaltyRuleRequest;
import com.example.hrm.modules.penalty.dto.response.PenaltyRuleResponse;
import com.example.hrm.modules.penalty.service.PenaltyRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/penalty-rules")
@RequiredArgsConstructor
public class PenaltyRuleController {

    private final PenaltyRuleService penaltyRuleService;

    @PostMapping
    public ResponseEntity<PenaltyRuleResponse> create(@Valid @RequestBody PenaltyRuleRequest request) {
        return ResponseEntity.ok(penaltyRuleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PenaltyRuleResponse> update(
            @PathVariable String id,
            @Valid @RequestBody PenaltyRuleRequest request
    ) {
        return ResponseEntity.ok(penaltyRuleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        penaltyRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PenaltyRuleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(penaltyRuleService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<PenaltyRuleResponse>> getAll() {
        return ResponseEntity.ok(penaltyRuleService.getAll());
    }

    @GetMapping("/active/list")
    public ResponseEntity<List<PenaltyRuleResponse>> getAllActive() {
        return ResponseEntity.ok(penaltyRuleService.getAllActive());
    }
}
