package com.example.hrm.modules.leave.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LeaveUtil {
    public static BigDecimal calculateTotalDays(LocalDate start, LocalDate end) {
        return BigDecimal.valueOf(ChronoUnit.DAYS.between(start, end) + 1);
    }
}
