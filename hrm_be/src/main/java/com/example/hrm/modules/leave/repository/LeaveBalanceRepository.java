package com.example.hrm.modules.leave.repository;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.leave.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, String> {

    Optional<LeaveBalance> findByEmployeeAndYearAndIsDeletedFalse(Employee employee, int year);

    List<LeaveBalance> findAllByYearAndIsDeletedFalse(int year);
}
