package com.example.hrm.shared.enums;

public enum RefType {
    EMPLOYEE("EMPLOYEE"),   // File gắn với nhân viên
    CONTRACT("CONTRACT"),   // File gắn với hợp đồng
    PROJECT("PROJECT");     // File gắn với dự án

    private final String value;

    // Constructor enum phải là private hoặc package-private
    RefType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
