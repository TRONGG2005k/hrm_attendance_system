package com.example.hrm.modules.payroll.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for import errors
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportErrorDto {
    Integer rowNumber;
    String message;
}
