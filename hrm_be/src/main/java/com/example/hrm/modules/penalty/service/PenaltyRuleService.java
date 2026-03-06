package com.example.hrm.modules.penalty.service;

import com.example.hrm.modules.penalty.dto.request.PenaltyRuleRequest;
import com.example.hrm.modules.penalty.dto.response.PenaltyRuleResponse;

import java.util.List;

public interface PenaltyRuleService {

    PenaltyRuleResponse create(PenaltyRuleRequest request);

    PenaltyRuleResponse update(String id, PenaltyRuleRequest request);

    void delete(String id);

    PenaltyRuleResponse getById(String id);

    List<PenaltyRuleResponse> getAll();

    List<PenaltyRuleResponse> getAllActive();
}
