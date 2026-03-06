package com.example.hrm.modules.leave.service;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.leave.entity.LeaveBalance;
import com.example.hrm.modules.leave.repository.LeaveBalanceRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    /**
     * Lấy hoặc tạo LeaveBalance cho nhân viên trong năm.
     */
    public LeaveBalance getOrCreate(Employee employee, int year) {
        return leaveBalanceRepository
                .findByEmployeeAndYearAndIsDeletedFalse(employee, year)
                .orElseGet(() -> leaveBalanceRepository.save(
                        LeaveBalance.builder()
                                .employee(employee)
                                .year(year)
                                .totalEntitled(0)
                                .used(0)
                                .remaining(0)
                                .build()
                ));
    }

    /**
     * Cộng phép năm mỗi tháng (+1).
     */
    public void accrueMonthlyLeave(Employee employee, int year) {
        LeaveBalance balance = getOrCreate(employee, year);

        balance.setTotalEntitled(balance.getTotalEntitled() + 1);
        balance.setRemaining(balance.getRemaining() + 1);

        leaveBalanceRepository.save(balance);
    }

    /**
     * Trừ phép khi duyệt nghỉ phép năm.
     */
    public void deductLeave(Employee employee, LocalDate startDate, LocalDate endDate) {
        int year = startDate.getYear();
        LeaveBalance balance = getOrCreate(employee, year);

        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (balance.getRemaining() < days) {
            throw new AppException(ErrorCode.NOT_ENOUGH_LEAVE, 400,
                    "Không đủ phép năm để nghỉ");
        }

        balance.setUsed(balance.getUsed() + days);
        balance.setRemaining(balance.getRemaining() - days);

        leaveBalanceRepository.save(balance);
    }

    /**
     * Lấy phép còn lại cho frontend.
     */
    public LeaveBalance getLeaveBalance(Employee employee, int year) {
        return getOrCreate(employee, year);
    }
}
