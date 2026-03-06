package com.example.hrm.modules.contract.service;

import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.entity.Allowance;
import com.example.hrm.modules.contract.mapper.AllowanceMapper;
import com.example.hrm.modules.contract.repository.AllowanceRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowanceService {

    private final AllowanceRepository repository;
    private final AllowanceMapper mapper;

    public AllowanceResponse create(AllowanceRequest request) {

        if (repository.existsByCodeAndActiveTrue(request.getCode())) {
            throw new AppException(
                    ErrorCode.ALLOWANCE_CODE_EXISTS, 400
            );
        }

        Allowance allowance = mapper.toEntity(request);
        allowance.setActive(true);

        return mapper.toResponse(repository.save(allowance));
    }

    public List<AllowanceResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public AllowanceResponse getById(String id) {
        Allowance allowance = repository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.ALLOWANCE_NOT_FOUND, 404
                ));

        return mapper.toResponse(allowance);
    }

    public AllowanceResponse update(String id, AllowanceRequest request) {
        Allowance existingAllowance = repository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.ALLOWANCE_NOT_FOUND, 404
                ));

        // Check if code is being changed and if it conflicts with another allowance
        if (!existingAllowance.getCode().equals(request.getCode()) &&
            repository.existsByCodeAndActiveTrue(request.getCode())) {
            throw new AppException(
                    ErrorCode.ALLOWANCE_CODE_EXISTS, 400
            );
        }

        existingAllowance.setCode(request.getCode());
        existingAllowance.setName(request.getName());
        existingAllowance.setDescription(request.getDescription());

        return mapper.toResponse(repository.save(existingAllowance));
    }

    public void delete(String id) {
        Allowance allowance = repository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.ALLOWANCE_NOT_FOUND, 404
                ));

        allowance.setActive(false);
        repository.save(allowance);
    }
}
