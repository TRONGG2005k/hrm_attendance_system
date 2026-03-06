package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.dto.request.WardRequest;
import com.example.hrm.modules.employee.dto.response.WardResponse;
import com.example.hrm.modules.employee.entity.District;
import com.example.hrm.modules.employee.entity.Ward;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.mapper.WardMapper;
import com.example.hrm.modules.employee.repository.DistrictRepository;
import com.example.hrm.modules.employee.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository wardRepository;
    private final DistrictRepository districtRepository;
    private final WardMapper wardMapper;

    @Transactional
    public WardResponse createWard(WardRequest request) {
        District district = districtRepository.findById(request.getDistrictId())
                .filter(d -> !d.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND, 404));

        Ward ward = wardMapper.toEntity(request);
        ward.setDistrict(district);
        wardRepository.save(ward);
        return wardMapper.toResponse(ward);
    }

    @Transactional
    public Page<WardResponse> getAllWards(Pageable pageable) {
        return wardRepository.findByIsDeletedFalse(pageable)
                .map(wardMapper::toResponse);
    }

    @Transactional
    public Page<WardResponse> getWardsByDistrict(String districtId, Pageable pageable) {
        return wardRepository.findByDistrictIdAndIsDeletedFalse(districtId, pageable)
                .map(wardMapper::toResponse);
    }

    @Transactional
    public WardResponse getWardById(String id) {
        Ward ward = wardRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND, 404));
        return wardMapper.toResponse(ward);
    }

    @Transactional
    public WardResponse updateWard(String id, WardRequest request) {
        Ward ward = wardRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND, 404));

        District district = districtRepository.findById(request.getDistrictId())
                .filter(d -> !d.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND, 404));

        wardMapper.updateEntity(request, ward);
        ward.setDistrict(district);
        wardRepository.save(ward);
        return wardMapper.toResponse(ward);
    }

    @Transactional
    public void deleteWard(String id) {
        Ward ward = wardRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND, 404));
        ward.setIsDeleted(true);
        ward.setDeletedAt(java.time.LocalDateTime.now());
        wardRepository.save(ward);
    }
}
