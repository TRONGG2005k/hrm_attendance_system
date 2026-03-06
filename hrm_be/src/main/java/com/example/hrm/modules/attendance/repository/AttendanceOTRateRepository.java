package com.example.hrm.modules.attendance.repository;

import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceOTRateRepository extends JpaRepository<AttendanceOTRate, String> {
    Page<AttendanceOTRate> findByIsDeletedFalse(Pageable pageable);
    Page<AttendanceOTRate> findByAttendanceIdAndIsDeletedFalse(String attendanceId, Pageable pageable);
}
