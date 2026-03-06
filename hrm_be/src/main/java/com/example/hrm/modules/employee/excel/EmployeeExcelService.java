package com.example.hrm.modules.employee.excel;

import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelExportDto;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.modules.employee.excel.validator.EmployeeValidator;
import com.example.hrm.modules.employee.excel.mapper.EmployeeExcelMapper;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmployeeExcelService {


    private final EmployeeValidator employeeValidator;
    private final EmployeeExcelMapper employeeExcelMapper;
    private final EmployeeRepository employeeRepository;
    private final ExcelHelper excelHelper;

//    @Transactional
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ExcelResult importEmployees(MultipartFile file) {


        List<EmployeeExcelImportDto> dtos = parseExcel(file);

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int rowNumber = 2; // nếu dòng 1 là header
        for (EmployeeExcelImportDto dto : dtos) {
            List<String> rowErrors = employeeValidator.validateEmployee(dto, rowNumber);

            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            try {
                Employee employee = employeeExcelMapper.toEntity(dto);
                employeeRepository.save(employee); // lưu từng dòng
                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Mã nhân viên đã tồn tại trong DB: " + ex.getMessage());
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi lưu dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelResult(successCount, errors);
    }

//    @Transactional
    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ExcelResult importOrUpdateEmployees(MultipartFile file) {
        List<EmployeeExcelImportDto> dtos = parseExcel(file);

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        int rowNumber = 2; // dòng 1 là header
        for (EmployeeExcelImportDto dto : dtos) {

            // 1. Validate dữ liệu
            List<String> rowErrors = employeeValidator.validateEmployee(dto, rowNumber);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            try {
                // 2. Check tồn tại theo code
                var existingOpt = employeeRepository.findByCodeAndIsDeletedFalse(dto.getCode());

                if (existingOpt.isPresent()) {
                    // 3a. Update
                    Employee existing = existingOpt.get();
                    employeeExcelMapper.updateEntity(existing, dto);
                    employeeRepository.save(existing);
                } else {
                    // 3b. Create mới
                    Employee employee = employeeExcelMapper.toEntity(dto);
                    employeeRepository.save(employee);
                }

                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Dữ liệu vi phạm ràng buộc DB");
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi xử lý dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelResult(successCount, errors);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public void export(OutputStream outputStream) throws IOException {

        List<Employee> employees = employeeRepository.findAllByIsDeletedFalse();
        Workbook workbook = new XSSFWorkbook();
        String sheetName = "Employees_" + LocalDateTime.now().toLocalDate();
        Sheet sheet = workbook.createSheet(sheetName);

        Row header = sheet.createRow(0);

        String[] headers = {
                "Code",
                "First Name",
                "Last Name",
                "Date of Birth",
                "Gender",
                "Email",
                "Phone",
                "Status",
                "Join Date",
                "Shift Type",
                "Street",
                "Ward",
                "District",
                "Province",
                "Department Name",
                "Position Name"
        };

        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;

        for (var employee : employees){
            Row row = sheet.createRow(rowIndex++);
            EmployeeExcelExportDto excelExportDto = employeeExcelMapper.toDto(employee);
            buildRow(row, excelExportDto);
        }
        workbook.write(outputStream);
        workbook.close();

    }
    private  List<EmployeeExcelImportDto> parseExcel(MultipartFile file) {
        List<EmployeeExcelImportDto> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            dtos = buildToDto(sheet);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return dtos;
    }

    public List<EmployeeExcelImportDto> buildToDto(Sheet sheet) {
        List<EmployeeExcelImportDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // dòng 0 là header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            EmployeeExcelImportDto dto = new EmployeeExcelImportDto();
            dto.setCode(excelHelper.getString(row.getCell(0)));
            dto.setFirstName(excelHelper.getString(row.getCell(1)));
            dto.setLastName(excelHelper.getString(row.getCell(2)));
            dto.setDateOfBirth(excelHelper.getLocalDate(row.getCell(3)));
            dto.setGender(excelHelper.getString(row.getCell(4)));
            dto.setEmail(excelHelper.getString(row.getCell(5)));
            dto.setPhone(excelHelper.getString(row.getCell(6)));
            dto.setStatus(excelHelper.getString(row.getCell(7)));
            dto.setJoinDate(excelHelper.getLocalDate(row.getCell(8)));
            dto.setShiftType(excelHelper.getShiftType(row.getCell(9)));

            dto.setStreet(excelHelper.getString(row.getCell(10)));
            dto.setWard(excelHelper.getString(row.getCell(11)));
            dto.setDistrict(excelHelper.getString(row.getCell(12)));
            dto.setProvince(excelHelper.getString(row.getCell(13)));

            dto.setDepartmentName(excelHelper.getString(row.getCell(14)));
            dto.setPositionName(excelHelper.getString(row.getCell(15)));

            dtos.add(dto);
        }
        return dtos;
    }

    public void buildRow(Row row, EmployeeExcelExportDto dto){
        row.createCell(0).setCellValue(dto.getCode());
        row.createCell(1).setCellValue(dto.getFirstName());
        row.createCell(2).setCellValue(dto.getLastName());
        row.createCell(3).setCellValue(
                dto.getDateOfBirth() != null ? dto.getDateOfBirth().toString() : ""
        );
        row.createCell(4).setCellValue(dto.getGender());
        row.createCell(5).setCellValue(dto.getEmail());
        row.createCell(6).setCellValue(dto.getPhone());
        row.createCell(7).setCellValue(dto.getStatus());
        row.createCell(8).setCellValue(dto.getJoinDate().toString());
        row.createCell(9).setCellValue(dto.getShiftType().name());
        row.createCell(10).setCellValue(dto.getStreet());
        row.createCell(11).setCellValue(dto.getWard());
        row.createCell(12).setCellValue(dto.getDistrict());
        row.createCell(13).setCellValue(dto.getProvince());
        row.createCell(14).setCellValue(dto.getDepartmentName());
        row.createCell(15).setCellValue(dto.getPositionName());

    }
}
