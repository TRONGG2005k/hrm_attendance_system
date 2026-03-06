package com.example.hrm.modules.contract.dto.response;

import com.example.hrm.shared.enums.ContractType;
import com.example.hrm.modules.employee.dto.response.EmployeeResponse;
import com.example.hrm.modules.file.dto.response.FileAttachmentResponse;
import com.example.hrm.shared.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {

    String id;

    EmployeeResponse employee;

    String code;

    ContractType type;

    LocalDate signDate;

    LocalDate startDate;

    LocalDate endDate;

    Double baseSalary;

    ContractStatus status;

    String note;

    List<FileAttachmentResponse> fileAttachmentResponses;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
