package com.example.hrm.modules.attendance.repository;

import com.example.hrm.modules.attendance.entity.OTRate;
import com.example.hrm.shared.enums.OTType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OTRateRepository extends JpaRepository<OTRate, String> {
    Page<OTRate> findByIsDeletedFalse(Pageable pageable);
    Page<OTRate> findByTypeAndIsDeletedFalse(OTType type, Pageable pageable);
    Optional<OTRate> findByDateAndTypeAndIsDeletedFalse(LocalDate date, OTType type);
    Optional<OTRate> findByTypeAndIsDeletedFalse( OTType type);
    List<OTRate> findAllByIsDeletedFalse();
    boolean existsByDateAndTypeAndIsDeletedFalse(LocalDate date, OTType type);
}
