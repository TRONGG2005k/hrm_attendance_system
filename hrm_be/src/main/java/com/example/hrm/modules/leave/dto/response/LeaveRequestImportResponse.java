package com.example.hrm.modules.leave.dto.response;

import com.example.hrm.modules.payroll.dto.response.ImportErrorDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for leave request import operation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestImportResponse {
    Integer totalRows;
    Integer successCount;
    Integer errorCount;
    List<ImportErrorDto> errors;
    List<String> importedLeaveRequestIds;
}
