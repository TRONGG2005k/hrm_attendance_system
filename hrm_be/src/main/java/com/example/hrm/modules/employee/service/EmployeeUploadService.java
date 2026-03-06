package com.example.hrm.modules.employee.service;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.file.dto.request.FileUploadRequest;
import com.example.hrm.modules.file.service.FileAttachmentService;
import com.example.hrm.modules.file.service.ZipExtractionService;
import com.example.hrm.shared.BulkUploadResult;
import com.example.hrm.shared.enums.RefType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeUploadService {

    private final EmployeeRepository employeeRepository;
    private final FileAttachmentService fileAttachmentService;
    private final ZipExtractionService zipExtractionService;

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public  BulkUploadResult importFile(MultipartFile file){

        List<String> errorMessages = new ArrayList<>();
        final int[] successCount = {0};
        final int[] errorCount = {0};
        final int[] totalCount = {0};

        try {
            zipExtractionService.extractAndProcess(
                    file,
                    (employeeCode,
                     category,
                     filename,
                     inputStream,
                     contentType, size) -> {
                        totalCount[0]++;

                        try {

                            Employee employee =
                                    employeeRepository
                                            .findByCodeAndIsDeletedFalse(employeeCode)
                                            .orElse(null);

                            if (employee == null) {

                                errorMessages.add(
                                        filename + ": không tìm thấy nhân viên"
                                );

                                errorCount[0]++;
                                return;
                            }


                            fileAttachmentService.uploadFile(
                                    FileUploadRequest.builder()
                                            .refType(RefType.EMPLOYEE.getValue())
                                            .refId(employee.getId())
                                            .filename(filename)
                                            .contentType(contentType)
                                            .size(size)
                                            .inputStream(inputStream)
                                            .category(category)
                                            .build()
                            );

                            successCount[0]++;

                        } catch (Exception e) {

                            errorMessages.add(
                                    filename + ": lỗi khi lưu file - " + e.getMessage()
                            );

                            errorCount[0]++;
                        }
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file zip", e);
        }

        return BulkUploadResult.builder()
                .total(totalCount[0])
                .success(successCount[0])
                .failed(errorCount[0])
                .errors(errorMessages)
                .build();
    }
}
