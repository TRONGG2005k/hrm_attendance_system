package com.example.hrm.modules.organization.excel;

// import com.example.hrm.modules.employee.excel.dto.EmployeeExcelExportDto;
import com.example.hrm.modules.organization.dto.request.DepartmentRequest;
import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.excel.dto.DepartmentExcelDto;
import com.example.hrm.modules.organization.excel.mapper.DepartmentExcelMapper;
import com.example.hrm.modules.organization.excel.validator.DepartmentExcelValidator;
import com.example.hrm.modules.organization.repository.DepartmentRepository;
import com.example.hrm.modules.organization.service.DepartmentService;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentExcelService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;
    private final DepartmentExcelValidator validator;
    private final DepartmentExcelMapper mapper;
    private final ExcelHelper excelHelper;

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public ExcelResult importFile(MultipartFile file) {
        List<DepartmentExcelDto> dtos = parseExcel(file);

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int rowNumber = 2;

        for (var dto : dtos) {
            List<String> rowErrors = validator.valid(dto, rowNumber);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            try {
                var existing = departmentRepository.findByNameAndIsDeletedFalse(dto.getName());

                if (existing != null) {
                    departmentService.updateDepartment(existing.getId(),
                            DepartmentRequest.builder()
                                    .name(dto.getName())
                                    .description(dto.getDescription())
                                    .build());
                } else {
                    Department department = mapper.toDepartment(dto);
                    departmentRepository.save(department);
                }

                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Tên phòng ban đã tồn tại");
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi lưu dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelResult(successCount, errors);
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public void exportFile(OutputStream outputStream) throws IOException {
        List<Department> departmentList = departmentRepository.findByIsDeletedFalse();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("department" + LocalDate.now());
        String[] headers = {
                "name",
                "description"
        };
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;

        for (var department : departmentList){
            Row row = sheet.createRow(rowIndex++);
            DepartmentExcelDto dto = mapper.toDto(department);
            buildRow(row, dto);
        }

        workbook.write(outputStream);
        workbook.close();

    }
    public List<DepartmentExcelDto> parseExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            return buildToDto(sheet);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
    }

    private List<DepartmentExcelDto> buildToDto(Sheet sheet) {
        List<DepartmentExcelDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            DepartmentExcelDto dto = new DepartmentExcelDto();
            dto.setName(excelHelper.getString(row.getCell(0)));
            dto.setDescription(excelHelper.getString(row.getCell(1)));
            dtos.add(dto);
        }
        return dtos;
    }

    public void buildRow(Row row, DepartmentExcelDto dto){
        row.createCell(0).setCellValue(dto.getName());
        row.createCell(1).setCellValue(dto.getDescription());
    }
}
