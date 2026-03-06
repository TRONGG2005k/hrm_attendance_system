package com.example.hrm.modules.penalty.entity;

import com.example.hrm.shared.enums.PenaltyType;

public interface PenaltySource {
    PenaltyType getPenaltyType();

    int getValue();           // phút / ngày / lần
}
