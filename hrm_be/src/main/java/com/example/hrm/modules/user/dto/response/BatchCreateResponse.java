package com.example.hrm.modules.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchCreateResponse {
    private List<UserAccountResponse> listRp;
}
