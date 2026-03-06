package com.example.hrm.modules.organization.excel;

import com.example.hrm.modules.organization.dto.request.SubDepartmentRequest;
import com.example.hrm.modules.organization.entity.Department;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.modules.organization.excel.dto.SubDepartmentExcelDto;
import com.example.hrm.modules.organization.excel.mapper.SubDepartmentExcelMapper;
import com.example.hrm.modules.organization.excel.validator.SubDepartmentExcelValidator;
import com.example.hrm.modules.organization.repository.DepartmentRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.modules.organization.service.SubDepartmentService;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubDepartmentExcelService {

    private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final SubDepartmentService subDepartmentService;
    private final SubDepartmentExcelValidator validator;
    private final SubDepartmentExcelMapper mapper;
    private final ExcelHelper excelHelper;

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public ExcelResult importFile(MultipartFile file) {
        List<SubDepartmentExcelDto> dtos = parseExcel(file);

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
                // Tìm Department theo tên
                Department department = departmentRepository.findByNameAndIsDeletedFalse(dto.getDepartmentName());
                if (department == null) {
                    errors.add("Dòng " + rowNumber + ": Department '" + dto.getDepartmentName() + "' không tồn tại");
                    rowNumber++;
                    continue;
                }
                // Kiểm tra trùng lặp SubDepartment theo (department + name)
                Optional<SubDepartment> existingOpt = subDepartmentRepository
                        .findByDepartmentAndNameAndIsDeletedFalse(department, dto.getName());
                
                if (existingOpt.isPresent()) {
                    // Cập nhật
                    SubDepartment existing = existingOpt.get();
                    subDepartmentService.updateSubDepartment(existing.getId(),
                            SubDepartmentRequest.builder()
                                    .name(dto.getName())
                                    .departmentId(department.getId())
                                    .description(dto.getDescription())
                                    .build());
                } else {
                    // Tạo mới
                    SubDepartment subDepartment = mapper.toSubDepartment(dto, department);
                    subDepartmentRepository.save(subDepartment);
                }

                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Tên sub-department đã tồn tại trong department này");
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi lưu dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelResult(successCount, errors);
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public void exportFile(OutputStream outputStream) throws IOException {
        List<SubDepartment> subDepartmentList = subDepartmentRepository.findByIsDeletedFalse();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sub-department" + LocalDate.now());
        String[] headers = {
                "name",
                "departmentName",
                "description"
        };
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;

        for (var subDepartment : subDepartmentList){
            Row row = sheet.createRow(rowIndex++);
            SubDepartmentExcelDto dto = mapper.toDto(subDepartment);
            buildRow(row, dto);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    public List<SubDepartmentExcelDto> parseExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            return buildToDto(sheet);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
    }

    private List<SubDepartmentExcelDto> buildToDto(Sheet sheet) {
        List<SubDepartmentExcelDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            SubDepartmentExcelDto dto = new SubDepartmentExcelDto();
            dto.setName(excelHelper.getString(row.getCell(0)));
            dto.setDepartmentName(excelHelper.getString(row.getCell(1)));
            dto.setDescription(excelHelper.getString(row.getCell(2)));
            dtos.add(dto);
        }
        return dtos;
    }

    public void buildRow(Row row, SubDepartmentExcelDto dto){
        row.createCell(0).setCellValue(dto.getName());
        row.createCell(1).setCellValue(dto.getDepartmentName());
        row.createCell(2).setCellValue(dto.getDescription());
    }
}
