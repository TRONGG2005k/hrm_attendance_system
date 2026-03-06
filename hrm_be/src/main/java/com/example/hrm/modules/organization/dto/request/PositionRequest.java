package com.example.hrm.modules.organization.dto.request;

public record PositionRequest(
        String code,
        String name,
        String description,
        Boolean active
) {}
