package com.example.hrm.modules.leave.excel;

import com.example.hrm.modules.attendance.service.AttendanceService;
import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.modules.leave.excel.dto.LeaveImportRequestExcelDto;
import com.example.hrm.modules.leave.excel.mapper.LeaveExcelMapper;
import com.example.hrm.modules.leave.excel.validator.LeaveExcelValidator;
import com.example.hrm.modules.leave.repository.LeaveRequestRepository;
import com.example.hrm.modules.user.entity.UserAccount;
import com.example.hrm.modules.user.repository.UserAccountRepository;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestExcelService {

    private final LeaveExcelValidator validator;
    private final LeaveExcelMapper mapper;
    private final UserAccountRepository userAccountRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceService attendanceService;

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ExcelResult importExcel(MultipartFile file){
        List<LeaveImportRequestExcelDto> dtos = parseExcel(file);
        int successCount = 0;
        int rowNumber = 2; // nếu dòng 1 là header
        List<String> errors = new ArrayList<>();
        UserAccount user = userAccountRepository.findByUsernameAndIsDeletedFalse(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 400));

        for(var dto : dtos) {
            List<String> rowErrors = validator.validateLeave(dto, rowNumber);
            errors.addAll(rowErrors);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            LeaveRequest leaveRequest = mapper.toEntity(dto);
            leaveRequest.setApprovedBy(user.getEmployee());
            leaveRequest.setApprovedAt(LocalDateTime.now());
            leaveRequestRepository.save(leaveRequest);

            attendanceService.generateFromLeave(leaveRequest);

            successCount++;
            rowNumber++;
        }
        return new ExcelResult(successCount, errors);
    }

    private  List<LeaveImportRequestExcelDto> parseExcel(MultipartFile file) {
        List<LeaveImportRequestExcelDto> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            dtos.addAll(mapper.toDto(sheet));

        } catch (Exception e) {
            log.error("Lỗi đọc file Excel: {}", e.getMessage());
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return dtos;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ByteArrayInputStream exportLeaveRequestsToExcel(String subDepartmentId) {
        String[] LEAVE_EXCEL_HEADERS = {
                "Mã nhân viên",
                "Ngày bắt đầu",
                "Ngày kết thúc",
                "Loại nghỉ",
                "Lý do"
        };

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployee_SubDepartment_IdAndIsDeletedFalse(subDepartmentId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Leave Requests");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < LEAVE_EXCEL_HEADERS.length; col++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(col);
                cell.setCellValue(LEAVE_EXCEL_HEADERS[col]);
            }

            // Data
            int rowIdx = 1;
            for (LeaveRequest leaveRequest : leaveRequests) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(leaveRequest.getEmployee().getCode());
                row.createCell(1).setCellValue(leaveRequest.getStartDate().toString());
                row.createCell(2).setCellValue(leaveRequest.getEndDate().toString());
                row.createCell(3).setCellValue(leaveRequest.getType().toString());
                row.createCell(4).setCellValue(leaveRequest.getReason());
            }

            // Auto-size columns
            for (int col = 0; col < LEAVE_EXCEL_HEADERS.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, 500, "Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }

}
