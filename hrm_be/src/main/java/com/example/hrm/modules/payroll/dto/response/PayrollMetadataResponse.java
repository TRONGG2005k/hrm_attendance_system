package com.example.hrm.modules.payroll.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayrollMetadataResponse {

    private String status; // DRAFT / FINAL / PAID

    private Integer version;

    private LocalDateTime calculatedAt;

    private String calculatedBy;

    private String note;
}
