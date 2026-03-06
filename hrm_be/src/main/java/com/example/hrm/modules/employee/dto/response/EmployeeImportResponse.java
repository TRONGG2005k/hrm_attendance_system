package com.example.hrm.modules.employee.dto.response;

import com.example.hrm.modules.payroll.dto.response.ImportErrorDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for employee import operation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeImportResponse {
    Integer totalRows;
    Integer successCount;
    Integer errorCount;
    List<ImportErrorDto> errors;
    List<String> importedEmployeeIds;
}
