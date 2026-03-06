package com.example.hrm.modules.penalty.repository;

import com.example.hrm.modules.penalty.entity.AttendancePenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendancePenaltyRepository extends JpaRepository<AttendancePenalty, String> {

    boolean existsByAttendance_Id(String attendanceId);

    Optional<AttendancePenalty> findByAttendance_Id(String attendanceId);

    // 1. Lấy tất cả penalty của một nhân viên trong khoảng thời gian
    @Query("SELECT p FROM AttendancePenalty p " +
            "WHERE p.attendance.employee.id = :employeeId " +
            "AND p.attendance.workDate BETWEEN :startDate AND :endDate")
    List<AttendancePenalty> findAllByEmployeeAndWorkDateBetween(
            @Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 2. Lấy tất cả penalty trong tháng bất kỳ (theo workDate)
    @Query("SELECT p FROM AttendancePenalty p " +
            "WHERE FUNCTION('MONTH', p.attendance.workDate) = :month " +
            "AND FUNCTION('YEAR', p.attendance.workDate) = :year")
    List<AttendancePenalty> findAllByMonth(
            @Param("month") int month,
            @Param("year") int year
    );

}
