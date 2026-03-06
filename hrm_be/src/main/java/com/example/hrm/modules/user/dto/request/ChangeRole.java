package com.example.hrm.modules.user.dto.request;

import com.example.hrm.shared.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ChangeRole {
    private Map<String, Boolean> changeRole;
}
