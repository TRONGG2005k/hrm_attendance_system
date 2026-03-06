package com.example.hrm.modules.leave.repository;

import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.shared.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {
    Optional<LeaveRequest> findByIdAndIsDeletedFalse(String id);

    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee WHERE lr.isDeleted = false")
    Page<LeaveRequest> findAllByIsDeletedFalse(Pageable pageable );

    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee WHERE lr.status = :status AND lr.isDeleted = false")
    Page<LeaveRequest> findAllByStatusAndIsDeletedFalse(Pageable pageable, LeaveStatus status);

    @Query("SELECT COUNT(lr) > 0 FROM LeaveRequest lr WHERE lr.employee.id = :employeeId AND lr.status = :status AND lr.isDeleted = false AND " +
           "((lr.startDate <= :endDate AND lr.endDate >= :startDate)) AND lr.id != :excludeId")
    boolean existsOverlappingApprovedLeave(@Param("employeeId") String employeeId, @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate, @Param("status") LeaveStatus status,
                                           @Param("excludeId") String excludeId);

    boolean existsByEmployee_CodeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsDeletedFalse(
            String code, LocalDate start, LocalDate end
    );

    List<LeaveRequest> findByEmployee_SubDepartment_IdAndIsDeletedFalse(String subDepartmentId);
}
