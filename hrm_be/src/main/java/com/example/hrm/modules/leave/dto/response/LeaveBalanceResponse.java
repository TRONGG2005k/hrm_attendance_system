package com.example.hrm.modules.leave.dto.response;

public record LeaveBalanceResponse(
        int year,
        int totalEntitled,
        int used,
        int remaining
) {}

