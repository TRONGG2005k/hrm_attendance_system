package com.example.hrm.modules.organization.repository;

import com.example.hrm.modules.organization.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    Page<Department> findByIsDeletedFalse(Pageable pageable);
    Department findByNameAndIsDeletedFalse(String name);
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.subDepartments WHERE d.id = :id")
    Optional<Department> findByIdWithSubDepartments(@Param("id") String id);
    List<Department> findByIsDeletedFalse();
    boolean existsByNameAndIsDeletedFalseAndIdNot(String code, String id);
}
