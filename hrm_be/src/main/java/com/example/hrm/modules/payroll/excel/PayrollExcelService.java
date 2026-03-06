package com.example.hrm.modules.payroll.excel;

import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.shared.enums.PayrollStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollExcelService {

    private final PayrollRepository payrollRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    /**
     * Export tất cả bảng lương
     */
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ByteArrayResource exportAllPayrolls() {
        List<Payroll> payrolls = payrollRepository.findAllByIsDeletedFalse();
        return exportPayrollsToExcel(payrolls, "All_Payrolls");
    }

    /**
     * Export bảng lương theo tháng
     */
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ByteArrayResource exportPayrollsByMonth(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<Payroll> payrolls = payrollRepository.findAllByMonthAndIsDeletedFalse(yearMonth);
        String sheetName = String.format("Payroll_%02d_%d", month, year);
        return exportPayrollsToExcel(payrolls, sheetName);
    }

    /**
     * Export bảng lương theo trạng thái và tháng
     */
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ByteArrayResource exportPayrollsByMonthAndStatus(int month, int year, PayrollStatus status) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<Payroll> payrolls = payrollRepository.findAllByMonthAndStatusAndIsDeletedFalse(yearMonth, status);
        String sheetName = String.format("Payroll_%02d_%d_%s", month, year, status.name());
        return exportPayrollsToExcel(payrolls, sheetName);
    }

    /**
     * Export bảng lương theo nhân viên
     */
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ByteArrayResource exportPayrollsByEmployee(String employeeId) {
        List<Payroll> payrolls = payrollRepository.findByEmployeeIdAndIsDeletedFalse(employeeId);
        return exportPayrollsToExcel(payrolls, "Employee_Payroll");
    }

    private ByteArrayResource exportPayrollsToExcel(List<Payroll> payrolls, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // ================= HEADER =================
            Row header = sheet.createRow(0);
            String[] headers = {
                    "STT",
                    "Mã nhân viên",
                    "Tên nhân viên",
                    "Phòng ban",
                    "Kỳ lương",
                    "Lương cơ bản",
                    "Phụ cấp",
                    "Tăng ca",
                    "Thưởng",
                    "Phạt",
                    "Nghỉ không lương",
                    "Tổng lương",
                    "Trạng thái",
                    "Ghi chú"
            };

            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // ================= DATA =================
            int rowIndex = 1;
            for (Payroll payroll : payrolls) {
                Row row = sheet.createRow(rowIndex++);
                buildPayrollRow(row, payroll, rowIndex - 1);
            }

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to memory
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            log.error("Export Excel error", e);
            throw new RuntimeException("Export Excel failed: " + e.getMessage(), e);
        }
    }

    private void buildPayrollRow(Row row, Payroll payroll, int stt) {
        // STT
        row.createCell(0).setCellValue(stt);

        // Mã nhân viên
        String employeeCode = payroll.getEmployee() != null ? payroll.getEmployee().getCode() : "";
        row.createCell(1).setCellValue(employeeCode);

        // Tên nhân viên
        String employeeName = "";
        if (payroll.getEmployee() != null) {
            employeeName = payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName();
        }
        row.createCell(2).setCellValue(employeeName.trim());

        // Phòng ban
        String departmentName = "";
        if (payroll.getEmployee() != null && payroll.getEmployee().getSubDepartment() != null) {
            departmentName = payroll.getEmployee().getSubDepartment().getName();
        }
        row.createCell(3).setCellValue(departmentName);

        // Kỳ lương
        String period = payroll.getMonth() != null ? payroll.getMonth().format(MONTH_FORMATTER) : "";
        row.createCell(4).setCellValue(period);

        // Lương cơ bản
        row.createCell(5).setCellValue(payroll.getBaseSalary() != null ? payroll.getBaseSalary().doubleValue() : 0);

        // Phụ cấp
        row.createCell(6).setCellValue(payroll.getAllowance() != null ? payroll.getAllowance().doubleValue() : 0);

        // Tăng ca
        row.createCell(7).setCellValue(payroll.getOvertime() != null ? payroll.getOvertime() : 0);

        // Thưởng
        row.createCell(8).setCellValue(payroll.getBonus() != null ? payroll.getBonus().doubleValue() : 0);

        // Phạt
        row.createCell(9).setCellValue(payroll.getPenalty() != null ? payroll.getPenalty().doubleValue() : 0);

        // Nghỉ không lương
        row.createCell(10).setCellValue(payroll.getUnpaidLeave() != null ? payroll.getUnpaidLeave().doubleValue() : 0);

        // Tổng lương
        row.createCell(11).setCellValue(payroll.getTotalSalary() != null ? payroll.getTotalSalary().doubleValue() : 0);

        // Trạng thái
        String status = payroll.getStatus() != null ? payroll.getStatus().name() : "";
        row.createCell(12).setCellValue(status);

        // Ghi chú
        String note = payroll.getNote() != null ? payroll.getNote() : "";
        row.createCell(13).setCellValue(note);
    }
}
