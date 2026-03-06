package com.example.hrm.modules.attendance.repository;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.shared.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    Page<Attendance> findByIsDeletedFalse(Pageable pageable);
    boolean existsByEmployeeAndWorkDate(Employee employee, LocalDate startDate);
    @Query("""
        select a
        from Attendance a
        join a.employee e
        join e.subDepartment s
        where s.id = :subDepartmentId
          and a.isDeleted = false
    """)
    Page<Attendance> findBySubDepartmentId(
            Pageable pageable,
            @Param("subDepartmentId") String subDepartmentId
    );

    /**
     * Tìm attendance với OT rates đã được fetch sẵn để tránh N+1 query
     */
    @Query("SELECT DISTINCT a FROM Attendance a " +
       "LEFT JOIN FETCH a.attendanceOTRates ot " +
       "LEFT JOIN FETCH ot.otRate r " +
       "WHERE a.employee.id = :employeeId " +
       "AND a.workDate BETWEEN :startDate AND :endDate " +
       "AND a.isDeleted = false " +
       "AND (ot.isDeleted = false OR ot.isDeleted IS NULL) " +
       "AND (r.isDeleted = false OR r.isDeleted IS NULL)")
    List<Attendance> findOTForEmployee(
            @Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<Attendance> findTopByEmployeeOrderByCheckInTimeDesc(Employee employee);

    Optional<Attendance> findByIdAndIsDeletedFalse(String id);

    Optional<Attendance> findTopByEmployeeAndStatusAndCheckOutTimeIsNullOrderByCheckInTimeDesc(Employee employee, AttendanceStatus attendanceStatus);

    Page<Attendance> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
}
