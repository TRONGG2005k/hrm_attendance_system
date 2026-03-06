package com.example.hrm.modules.organization.repository;

import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.entity.SubDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubDepartmentRepository extends JpaRepository<SubDepartment, String> {
    Page<SubDepartment> findByIsDeletedFalse(Pageable pageable);
    Page<SubDepartment> findByDepartmentIdAndIsDeletedFalse(String departmentId, Pageable pageable);
    SubDepartment findByDepartmentIdAndNameAndIsDeletedFalse(String departmentId, String name);
    Optional<SubDepartment> findByDepartmentAndNameAndIsDeletedFalse(Department department, String name);
    Optional<SubDepartment> findByIdAndIsDeletedFalse(String id);
    Optional<SubDepartment> findByNameAndIsDeletedFalse(String name);
    List<SubDepartment> findByIsDeletedFalse();
    boolean existsByNameAndIsDeletedFalse(String name);
}
