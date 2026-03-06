package com.example.hrm.modules.payroll.repository;

import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.shared.enums.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {
    Page<Payroll> findByIsDeletedFalse(Pageable pageable);

    Page<Payroll> findByIsDeletedFalseAndMonth(
            YearMonth month,
            Pageable pageable
    );

    Optional<Payroll> findByIdAndIsDeletedFalse(String id);

    Page<Payroll> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);

    Optional<Payroll> findByEmployeeIdAndMonthAndIsDeletedFalse(String employeeId, YearMonth month);

    boolean existsByEmployeeIdAndMonthAndIsDeletedFalse(String employeeId, YearMonth month);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee WHERE p.month = :month AND p.status = :status AND p.isDeleted = false")
    List<Payroll> findAllByMonthAndStatusAndIsDeletedFalse(@Param("month") YearMonth month,
            @Param("status") PayrollStatus status);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee WHERE p.month = :month AND p.isDeleted = false")
    List<Payroll> findAllByMonthAndIsDeletedFalse(@Param("month") YearMonth month);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee WHERE p.employee.id = :employeeId AND p.isDeleted = false")
    List<Payroll> findByEmployeeIdAndIsDeletedFalse(@Param("employeeId") String employeeId);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee WHERE p.isDeleted = false")
    List<Payroll> findAllByIsDeletedFalse();

    @Query("""
                SELECT COALESCE(SUM(p.totalSalary), 0)
                FROM Payroll p
                WHERE p.month = :month
                  AND p.status = :status
                  AND p.isDeleted = false
            """)
    Optional<BigDecimal> sumTotalSalaryByMonthAndStatusAndIsDeletedFalse(
            @Param("month") String month,
            @Param("status") PayrollStatus status);

}
