package com.example.hrm.modules.penalty;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.penalty.dto.response.AttendancePenaltyResult;
import com.example.hrm.modules.penalty.entity.PenaltyRule;
import com.example.hrm.shared.enums.BasedOn;
import com.example.hrm.shared.enums.PenaltyType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PenaltyAmountCalculator {

    private final PenaltyRuleResolver resolver;



    public AttendancePenaltyResult applyAttendancePenaltyRule(Attendance attendance) {

        long late = attendance.getLateMinutes();
        long early = attendance.getEarlyLeaveMinutes();

        PenaltyRule lateRule =
                resolver.resolve(BasedOn.MINUTE, late);
        PenaltyRule earlyRule =
                resolver.resolve(BasedOn.MINUTE, early);

        BigDecimal totalPenalty = BigDecimal.ZERO;
        boolean voidBaseSalary = false;
        boolean voidOt = false;

        if (lateRule != null) {
            totalPenalty = totalPenalty.add(lateRule.getPenaltyValue());
            voidBaseSalary |= lateRule.getPenaltyType() == PenaltyType.VOID_BASE_SALARY;
            voidOt |= lateRule.getPenaltyType() == PenaltyType.VOID_OVERTIME;
        }

        if (earlyRule != null) {
            totalPenalty = totalPenalty.add(earlyRule.getPenaltyValue());
            voidBaseSalary |= earlyRule.getPenaltyType() == PenaltyType.VOID_BASE_SALARY;
            voidOt |= earlyRule.getPenaltyType() == PenaltyType.VOID_OVERTIME;
        }

        return new AttendancePenaltyResult(
                totalPenalty.longValue(),
                voidBaseSalary,
                voidOt
        );
    }
}

