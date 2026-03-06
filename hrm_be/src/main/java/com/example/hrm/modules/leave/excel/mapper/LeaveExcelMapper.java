package com.example.hrm.modules.leave.excel.mapper;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.modules.leave.excel.dto.LeaveImportRequestExcelDto;
import com.example.hrm.modules.leave.util.LeaveUtil;
import com.example.hrm.shared.enums.LeaveStatus;
import com.example.hrm.shared.excel.ExcelHelper;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveExcelMapper {

    private final EmployeeRepository employeeRepository;
    private final ExcelHelper excelHelper;

    public List<LeaveImportRequestExcelDto> toDto(Sheet sheet) {
        List<LeaveImportRequestExcelDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // dòng 0 là header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            LeaveImportRequestExcelDto dto = new LeaveImportRequestExcelDto();
            dto.setEmployeeCode(excelHelper.getString(row.getCell(0)));
            dto.setStartDate(excelHelper.getLocalDate(row.getCell(1)));
            dto.setEndDate(excelHelper.getLocalDate(row.getCell(2)));
            dto.setType(excelHelper.getLeaveType(row.getCell(3)));
            dto.setReason(excelHelper.getString(row.getCell(4)));
            dtos.add(dto);
        }
        return dtos;
    }

    public LeaveRequest toEntity(LeaveImportRequestExcelDto dto){
        Employee employee = employeeRepository.findByCodeAndIsDeletedFalse(dto.getEmployeeCode())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setTotalDays(LeaveUtil.calculateTotalDays(dto.getStartDate(), dto.getEndDate()));
        leaveRequest.setType(dto.getType());
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setEmployee(employee);
        return leaveRequest;
    }
}
