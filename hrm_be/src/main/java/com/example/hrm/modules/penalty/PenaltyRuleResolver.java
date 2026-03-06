package com.example.hrm.modules.penalty;

import org.springframework.stereotype.Component;

import com.example.hrm.modules.penalty.entity.PenaltyRule;
import com.example.hrm.modules.penalty.repository.PenaltyRuleRepository;
import com.example.hrm.shared.enums.BasedOn;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PenaltyRuleResolver {

    private final PenaltyRuleRepository repository;

    public PenaltyRule resolve(BasedOn basedOn, long value) {

        return repository
                .findByBasedOnAndActiveTrueOrderByPriorityDesc(basedOn)
                .stream()
                .filter(rule -> match(rule, value))
                .findFirst()
                .orElse(null);
    }

    private boolean match(PenaltyRule rule, long value) {
        if (value < rule.getMinValue()) return false;
        if (rule.getMaxValue() == null) return true;
        return value <= rule.getMaxValue();
    }
}
