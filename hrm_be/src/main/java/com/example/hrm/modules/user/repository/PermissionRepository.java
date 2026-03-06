package com.example.hrm.modules.user.repository;

import com.example.hrm.modules.user.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    Page<Permission> findByIsDeletedFalse(Pageable pageable);
    Permission findByNameAndIsDeletedFalse(String name);
}
