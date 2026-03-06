package com.example.hrm.shared;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkUploadResult {

    private int total;

    private int success;

    private int failed;

    private List<String> errors;
}
