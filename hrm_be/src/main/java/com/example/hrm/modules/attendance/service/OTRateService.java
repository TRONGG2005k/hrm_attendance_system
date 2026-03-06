package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.request.OTRateRequest;
import com.example.hrm.modules.attendance.dto.response.OTRateResponse;
import com.example.hrm.modules.attendance.entity.OTRate;
import com.example.hrm.shared.enums.OTType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.mapper.OTRateMapper;
import com.example.hrm.modules.attendance.repository.OTRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTRateService {

    private final OTRateRepository otRateRepository;
    private final OTRateMapper otRateMapper;

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public OTRateResponse create(OTRateRequest request) {

        otRateRepository.findByDateAndTypeAndIsDeletedFalse(request.getDate(), request.getType())
                .ifPresent(e -> {
                    throw new AppException(ErrorCode.OT_RATE_DUPLICATE, 401);
                });

        OTRate otRate = otRateMapper.toEntity(request);
        return otRateMapper.toResponse(otRateRepository.save(otRate));
    }

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public OTRateResponse update(String id, OTRateRequest request) {
        OTRate otRate = otRateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        otRateMapper.updateEntityFromRequest( request, otRate);
        otRate.setUpdatedAt(LocalDateTime.now());

        return otRateMapper.toResponse(otRateRepository.save(otRate));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        OTRate otRate = otRateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        otRate.setIsDeleted(true);
        otRate.setDeletedAt(LocalDateTime.now());

        otRateRepository.save(otRate);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public OTRateResponse getById(String id) {
        OTRate otRate = otRateRepository.findById(id)
                .filter(e -> !e.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        return otRateMapper.toResponse(otRate);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public List<OTRateResponse> getAll() {
        return otRateRepository.findAllByIsDeletedFalse()
                .stream()
                .map(otRateMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public OTRateResponse getByDateAndType(LocalDate date, OTType type) {
        OTRate otRate = otRateRepository
                .findByDateAndTypeAndIsDeletedFalse(date, type)
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        return otRateMapper.toResponse(otRate);
    }
}
