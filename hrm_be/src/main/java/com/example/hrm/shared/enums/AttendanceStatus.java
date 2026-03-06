package com.example.hrm.shared.enums;

public enum AttendanceStatus {
    WORKING,     // đang làm việc (chưa checkout)
    COMPLETED,   // đã checkout
    LEAVE,       // nghỉ phép
    ABSENT    ,   // vắng không phép,
    LEAVE_UNPAID
}
