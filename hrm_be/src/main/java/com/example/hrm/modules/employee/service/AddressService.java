package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.dto.request.AddressRequest;
import com.example.hrm.modules.employee.dto.response.AddressResponse;
import com.example.hrm.modules.employee.entity.Address;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.mapper.AddressMapper;
import com.example.hrm.modules.employee.repository.AddressRepository;
import com.example.hrm.modules.employee.repository.WardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final WardRepository wardRepository;

    public Page<AddressResponse> getAllAddresses(int page, int size) {
        var addressPage = addressRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return addressPage.map(addressMapper::toResponse);
    }

    public AddressResponse getAddressById(String id) {
        var address = addressRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404, 
                                "Địa chỉ không tìm thấy với ID: " + id));
        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        if (request.getWardId() == null || request.getWardId().trim().isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_WARD_NOT_FOUND, 400, "Phường/xã không được để trống");
        }

        var ward = wardRepository.findByIdAndIsDeletedFalse(request.getWardId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_WARD_NOT_FOUND, 404, 
                        "Phường/xã không tìm thấy với ID: " + request.getWardId()));

        Address address = addressMapper.toEntity(request);
        if (address == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, 400, "Dữ liệu địa chỉ không hợp lệ");
        }

        address.setWard(ward);
        @SuppressWarnings("null")
        var savedAddress = addressRepository.save(address);
        return addressMapper.toResponse(savedAddress);
    }

    @Transactional
    public AddressResponse updateAddress(String id, AddressRequest request) {
        var address = addressRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404, 
                                "Địa chỉ không tìm thấy với ID: " + id));

        if (request.getWardId() != null && !request.getWardId().trim().isEmpty()) {
            var ward = wardRepository.findByIdAndIsDeletedFalse(request.getWardId())
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_WARD_NOT_FOUND, 404, 
                            "Phường/xã không tìm thấy với ID: " + request.getWardId()));
            address.setWard(ward);
        }

        addressMapper.updateEntity(request, address);
        @SuppressWarnings("null")
        var updatedAddress = addressRepository.save(address);
        return addressMapper.toResponse(updatedAddress);
    }

    @Transactional
    public void deleteAddress(String id) {
        var address = addressRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404, 
                                "Địa chỉ không tìm thấy với ID: " + id));
        address.setIsDeleted(true);
        addressRepository.save(address);
    }
}
