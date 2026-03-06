package com.example.hrm.modules.payroll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hrm.modules.payroll.entity.PayrollCycle;

@Repository
public interface PayrollCycleRepository
        extends JpaRepository<PayrollCycle, Long> {

    boolean existsByStartDayAndEndDay(Integer startDay, Integer endDay);

    Optional<PayrollCycle> findByActiveTrue();

    List<PayrollCycle> findAllByOrderByCreatedAtDesc();
}
