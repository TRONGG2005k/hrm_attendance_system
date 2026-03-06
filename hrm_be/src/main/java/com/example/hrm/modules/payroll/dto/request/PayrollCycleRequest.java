package com.example.hrm.modules.payroll.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayrollCycleRequest {

    @NotNull
    private Integer startDay;

    @NotNull
    private Integer endDay;

    @NotNull
    private Integer payday;
}

