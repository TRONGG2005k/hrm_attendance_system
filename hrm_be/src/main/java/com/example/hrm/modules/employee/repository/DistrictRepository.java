package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.District;
import com.example.hrm.modules.employee.entity.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    Page<District> findByIsDeletedFalse(Pageable pageable);
    Page<District> findByProvinceIdAndIsDeletedFalse(String provinceId, Pageable pageable);
    Optional<District> findByNameAndProvinceAndIsDeletedFalse(String name, Province province);
}
