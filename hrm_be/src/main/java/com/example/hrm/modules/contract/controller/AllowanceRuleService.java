package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.AllowanceRuleRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceRuleResponse;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.contract.mapper.AllowanceRuleMapper;
import com.example.hrm.modules.contract.repository.AllowanceRepository;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowanceRuleService {

    private final AllowanceRuleRepository repository;
    private final AllowanceRepository allowanceRepository;
    private final PositionRepository positionRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final AllowanceRuleMapper mapper;

    public AllowanceRuleResponse create(AllowanceRuleRequest request) {

        AllowanceRule rule = new AllowanceRule();

        rule.setAllowance(
                allowanceRepository.findById(request.getAllowanceId())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.NOT_FOUND, 404))
        );

        if (request.getPositionId() != null) {
            rule.setPosition(
                    positionRepository.findById(request.getPositionId())
                            .orElseThrow(() -> new AppException(
                                    ErrorCode.NOT_FOUND, 404))
            );
        }

        if (request.getSubDepartmentId() != null) {
            rule.setSubDepartment(
                    subDepartmentRepository.findByIdAndIsDeletedFalse(
                                    request.getSubDepartmentId())
                            .orElseThrow(() -> new AppException(
                                    ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404))
            );
        }

        rule.setAmount(request.getAmount());
        rule.setCalculationType(request.getCalculationType());
        rule.setActive(true);

        return mapper.toResponse(repository.save(rule));
    }
    public List<AllowanceRule> getAll(){
        return repository.findAllByActiveTrue();
    }
}
