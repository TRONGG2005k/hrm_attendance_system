package com.example.hrm.modules.user.repository;

import com.example.hrm.modules.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Page<Role> findByIsDeletedFalse(Pageable pageable);
    Optional<Role> findByNameAndIsDeletedFalse(String name);
    Optional<Role> findByIdAndIsDeletedFalse(String id);
}
