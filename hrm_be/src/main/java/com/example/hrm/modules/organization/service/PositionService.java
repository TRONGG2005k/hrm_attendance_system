package com.example.hrm.modules.organization.service;


import com.example.hrm.modules.organization.dto.request.PositionRequest;
import com.example.hrm.modules.organization.dto.response.PositionResponse;
import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.mapper.PositionMapper;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository repository;
    private final PositionMapper mapper;

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public PositionResponse create(PositionRequest request) {
        if (repository.existsByCode(request.code())) {
            throw new AppException(ErrorCode.DUPLICATE_CODE, 500);
        }

        Position position = mapper.toEntity(request);
        position.setIsDeleted(false);
        return mapper.toResponse(repository.save(position));
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public PositionResponse update(String id, PositionRequest request) {
        Position position = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));

        position.setName(request.name());
        position.setDescription(request.description());
        if (request.active() != null) {
            position.setActive(request.active());
        }

        return mapper.toResponse(repository.save(position));
    }

    @PreAuthorize("isAuthenticated()")
    public List<PositionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public PositionResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));
    }
}
