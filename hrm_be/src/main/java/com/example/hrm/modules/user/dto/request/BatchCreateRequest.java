package com.example.hrm.modules.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

public class BatchCreateRequest {
    private List<String> listId;
}
