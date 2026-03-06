package com.example.hrm.modules.penalty.dto.response;

public record LateAndEarly (
    Integer lateMinute,
    Integer earlyMinute
){}