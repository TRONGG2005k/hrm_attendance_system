package com.example.hrm.shared;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveAccrualJob {

    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceService leaveBalanceService;

    /**
     * Chạy lúc 00:05 ngày 1 hàng tháng
     */
    @Scheduled(cron = "0 5 0 1 * ?")
    public void accrueMonthlyLeaveForAllEmployees() {
        int year = LocalDate.now().getYear();

        List<Employee> employees = employeeRepository.findAllByIsDeletedFalse();

        for (Employee employee : employees) {
            leaveBalanceService.accrueMonthlyLeave(employee, year);
        }
    }
}
