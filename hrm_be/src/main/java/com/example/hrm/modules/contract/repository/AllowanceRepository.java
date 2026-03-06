package com.example.hrm.modules.contract.repository;

import com.example.hrm.modules.contract.entity.Allowance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllowanceRepository
        extends JpaRepository<Allowance, String> {

    boolean existsByCodeAndActiveTrue(String code);
}
