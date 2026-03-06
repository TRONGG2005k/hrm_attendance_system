package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.entity.Address;
import com.example.hrm.modules.employee.entity.District;
import com.example.hrm.modules.employee.entity.Province;
import com.example.hrm.modules.employee.entity.Ward;
import com.example.hrm.modules.employee.repository.AddressRepository;
import com.example.hrm.modules.employee.repository.DistrictRepository;
import com.example.hrm.modules.employee.repository.ProvinceRepository;
import com.example.hrm.modules.employee.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý việc tạo và resolve địa chỉ theo mô hình phân cấp
 * Province → District → Ward → Address
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressResolverService {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final AddressRepository addressRepository;

    /**
     * Resolve địa chỉ từ thông tin phân cấp
     * Nếu địa chỉ đã tồn tại thì reuse, nếu chưa thì tạo mới
     */
    @Transactional
    public Address resolveAddress(String provinceName, String districtName, String wardName, String street) {
        if (!hasAddressInfo(provinceName, districtName, wardName, street)) {
            return null;
        }

        // 1. Resolve Province
        Province province = resolveProvince(provinceName);

        // 2. Resolve District
        District district = resolveDistrict(districtName, province);

        // 3. Resolve Ward
        Ward ward = resolveWard(wardName, district);

        // 4. Resolve Address
        return resolveAddressEntity(street, ward);
    }

    private boolean hasAddressInfo(String provinceName, String districtName, String wardName, String street) {
        return provinceName != null && !provinceName.trim().isEmpty() &&
               districtName != null && !districtName.trim().isEmpty() &&
               wardName != null && !wardName.trim().isEmpty() &&
               street != null && !street.trim().isEmpty();
    }

    private Province resolveProvince(String provinceName) {
        return provinceRepository.findByNameAndIsDeletedFalse(provinceName.trim())
                .orElseGet(() -> {
                    Province newProvince = Province.builder()
                            .name(provinceName.trim())
                            .build();
                    return provinceRepository.save(newProvince);
                });
    }

    private District resolveDistrict(String districtName, Province province) {
        return districtRepository.findByNameAndProvinceAndIsDeletedFalse(districtName.trim(), province)
                .orElseGet(() -> {
                    District newDistrict = District.builder()
                            .name(districtName.trim())
                            .province(province)
                            .build();
                    return districtRepository.save(newDistrict);
                });
    }

    private Ward resolveWard(String wardName, District district) {
        return wardRepository.findByNameAndDistrictAndIsDeletedFalse(wardName.trim(), district)
                .orElseGet(() -> {
                    Ward newWard = Ward.builder()
                            .name(wardName.trim())
                            .district(district)
                            .build();
                    return wardRepository.save(newWard);
                });
    }

    private Address resolveAddressEntity(String street, Ward ward) {
        return addressRepository.findByStreetAndWardAndIsDeletedFalse(street.trim(), ward)
                .orElseGet(() -> {
                    Address newAddress = Address.builder()
                            .street(street.trim())
                            .ward(ward)
                            .build();
                    return addressRepository.save(newAddress);
                });
    }
}
