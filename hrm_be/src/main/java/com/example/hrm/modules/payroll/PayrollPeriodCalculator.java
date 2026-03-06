package com.example.hrm.modules.payroll;


import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Component;

import com.example.hrm.modules.payroll.dto.response.PayrollCycleResponse;

@Component
public class PayrollPeriodCalculator {

    public PayrollPeriod calculate(PayrollCycleResponse cycle, int year, int month) {
        YearMonth payrollMonth = YearMonth.of(year, month);

        int startDay = cycle.getStartDay();
        int endDay = cycle.getEndDay();

        LocalDate startDate;
        LocalDate endDate;

        if (startDay > endDay) {
            YearMonth previousMonth = payrollMonth.minusMonths(1);

            startDate = previousMonth.atDay(
                    Math.min(startDay, previousMonth.lengthOfMonth()));

            endDate = payrollMonth.atDay(
                    Math.min(endDay, payrollMonth.lengthOfMonth()));
        } else {
            startDate = payrollMonth.atDay(
                    Math.min(startDay, payrollMonth.lengthOfMonth()));

            endDate = payrollMonth.atDay(
                    Math.min(endDay, payrollMonth.lengthOfMonth()));
        }

        return new PayrollPeriod(startDate, endDate);
    }




    public record PayrollPeriod(
            LocalDate startDate,
            LocalDate endDate) {
    }
}
