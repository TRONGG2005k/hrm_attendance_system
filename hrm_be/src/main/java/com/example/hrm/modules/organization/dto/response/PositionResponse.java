package com.example.hrm.modules.organization.dto.response;

public record PositionResponse(
        String id,
        String code,
        String name,
        String description,
        Boolean active
) {}
