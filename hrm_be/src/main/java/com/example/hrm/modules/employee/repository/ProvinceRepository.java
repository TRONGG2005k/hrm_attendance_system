package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
    Page<Province> findByIsDeletedFalse(Pageable pageable);
    Optional<Province> findByNameAndIsDeletedFalse(String name);
}
