package com.example.hrm.modules.penalty.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendancePenaltyResult {

    private final long penaltyAmount;      // số tiền phạt
    private final boolean voidBaseSalary;   // mất lương ngày đó
    private final boolean voidOvertime;     // không tính OT
}
