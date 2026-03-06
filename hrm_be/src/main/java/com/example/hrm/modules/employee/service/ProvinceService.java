package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.dto.request.ProvinceRequest;
import com.example.hrm.modules.employee.dto.response.ProvinceResponse;
import com.example.hrm.modules.employee.entity.Province;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.mapper.ProvinceMapper;
import com.example.hrm.modules.employee.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProvinceService {

    private final ProvinceRepository provinceRepository;
    private final ProvinceMapper provinceMapper;

    @Transactional
    public ProvinceResponse createProvince(ProvinceRequest request) {
        Province province = provinceMapper.toEntity(request);
        provinceRepository.save(province);
        return provinceMapper.toResponse(province);
    }

    @Transactional
    public Page<ProvinceResponse> getAllProvinces(Pageable pageable) {
        return provinceRepository.findByIsDeletedFalse(pageable)
                .map(provinceMapper::toResponse);
    }

    @Transactional
    public ProvinceResponse getProvinceById(String id) {
        Province province = provinceRepository.findById(id)
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND, 404));
        return provinceMapper.toResponse(province);
    }

    @Transactional
    public ProvinceResponse updateProvince(String id, ProvinceRequest request) {
        Province province = provinceRepository.findById(id)
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND, 404));
        provinceMapper.updateEntity(request, province);
        provinceRepository.save(province);
        return provinceMapper.toResponse(province);
    }

    @Transactional
    public void deleteProvince(String id) {
        Province province = provinceRepository.findById(id)
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND, 404));
        province.setIsDeleted(true);
        province.setDeletedAt(java.time.LocalDateTime.now());
        provinceRepository.save(province);
    }
}

