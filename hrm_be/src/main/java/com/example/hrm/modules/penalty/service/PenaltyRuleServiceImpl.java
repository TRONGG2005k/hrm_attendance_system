package com.example.hrm.modules.penalty.service;

import com.example.hrm.modules.penalty.dto.request.PenaltyRuleRequest;
import com.example.hrm.modules.penalty.dto.response.PenaltyRuleResponse;
import com.example.hrm.modules.penalty.entity.PenaltyRule;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.penalty.mapper.PenaltyRuleMapper;
import com.example.hrm.modules.penalty.repository.PenaltyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyRuleServiceImpl implements PenaltyRuleService {

    private final PenaltyRuleRepository penaltyRuleRepository;
    private final PenaltyRuleMapper penaltyRuleMapper;

    @Override
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public PenaltyRuleResponse create(PenaltyRuleRequest request) {
        PenaltyRule penaltyRule = penaltyRuleMapper.toEntity(request);

        if (penaltyRule.getEffectiveFrom() == null) {
            penaltyRule.setEffectiveFrom(LocalDateTime.now());
        }

        penaltyRule.setCreatedAt(LocalDateTime.now());
        penaltyRule.setUpdatedAt(LocalDateTime.now());

        return penaltyRuleMapper.toResponse(penaltyRuleRepository.save(penaltyRule));
    }


    @Override
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public PenaltyRuleResponse update(String id, PenaltyRuleRequest request) {
        PenaltyRule penaltyRule = penaltyRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PENALTY_RULE_NOT_FOUND, 404));

        penaltyRuleMapper.updateEntityFromRequest(request, penaltyRule);
        penaltyRule.setUpdatedAt(LocalDateTime.now());

        return penaltyRuleMapper.toResponse(penaltyRuleRepository.save(penaltyRule));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        PenaltyRule penaltyRule = penaltyRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PENALTY_RULE_NOT_FOUND, 404));

        penaltyRuleRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public PenaltyRuleResponse getById(String id) {
        PenaltyRule penaltyRule = penaltyRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PENALTY_RULE_NOT_FOUND, 404));

        return penaltyRuleMapper.toResponse(penaltyRule);
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public List<PenaltyRuleResponse> getAll() {
        return penaltyRuleRepository.findAll()
                .stream()
                .map(penaltyRuleMapper::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public List<PenaltyRuleResponse> getAllActive() {
        return penaltyRuleRepository.findAllByActiveTrueOrderByPriorityAsc()
                .stream()
                .map(penaltyRuleMapper::toResponse)
                .toList();
    }
}
