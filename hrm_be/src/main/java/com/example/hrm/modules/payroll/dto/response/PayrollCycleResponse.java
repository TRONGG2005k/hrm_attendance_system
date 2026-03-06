package com.example.hrm.modules.payroll.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PayrollCycleResponse {

    private Long id;
    private Integer startDay;
    private Integer endDay;
    private Integer payday;
    private Integer workingDays;
    private Boolean active;
    private LocalDateTime createdAt;
}
