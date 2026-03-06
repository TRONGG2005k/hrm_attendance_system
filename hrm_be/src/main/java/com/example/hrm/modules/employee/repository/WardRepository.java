package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.District;
import com.example.hrm.modules.employee.entity.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    Page<Ward> findByIsDeletedFalse(Pageable pageable);
    Page<Ward> findByDistrictIdAndIsDeletedFalse(String districtId, Pageable pageable);
    Optional<Ward> findByIdAndIsDeletedFalse(String id);
    Optional<Ward> findByNameAndDistrictAndIsDeletedFalse(String name, District district);
}
