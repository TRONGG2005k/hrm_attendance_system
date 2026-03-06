package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.AllowanceRuleRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceRuleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api-prefix}/allowance-rules")
@RequiredArgsConstructor
public class AllowanceRuleController {

    private final AllowanceRuleService service;

    @PostMapping
    public AllowanceRuleResponse create(
            @RequestBody @Valid AllowanceRuleRequest request) {
        return service.create(request);
    }
}
