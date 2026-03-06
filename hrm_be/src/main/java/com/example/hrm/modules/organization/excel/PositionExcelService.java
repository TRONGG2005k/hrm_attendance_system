package com.example.hrm.modules.organization.excel;

import com.example.hrm.modules.organization.dto.request.PositionRequest;
import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.excel.dto.PositionExcelDto;
import com.example.hrm.modules.organization.excel.mapper.PositionExcelMapper;
import com.example.hrm.modules.organization.excel.validator.PositionExcelValidator;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.modules.organization.service.PositionService;
import com.example.hrm.shared.ExcelResult;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
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
public class PositionExcelService {

    private final PositionRepository positionRepository;
    private final PositionService positionService;
    private final PositionExcelValidator validator;
    private final PositionExcelMapper mapper;
    private final ExcelHelper excelHelper;

    @PreAuthorize("hasAnyRole('HR_MANAGER', 'ADMIN')")
    public ExcelResult importFile(MultipartFile file) {
        List<PositionExcelDto> dtos = parseExcel(file);

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
                var existing = positionRepository.findByCodeAndIsDeletedFalse(dto.getCode());

                if (existing.isPresent()) {
                    positionService.update(existing.get().getId(),
                            new PositionRequest(
                                    dto.getCode(),
                                    dto.getName(),
                                    dto.getDescription(),
                                    dto.getActive()
                            ));
                } else {
                    Position position = mapper.toPosition(dto);
                    positionRepository.save(position);
                }

                successCount++;
            } catch (DataIntegrityViolationException ex) {
                errors.add("Dòng " + rowNumber + ": Mã hoặc tên chức vụ đã tồn tại");
            } catch (Exception ex) {
                errors.add("Dòng " + rowNumber + ": Lỗi hệ thống khi lưu dữ liệu");
            }

            rowNumber++;
        }

        return new ExcelResult(successCount, errors);
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public void exportFile(OutputStream outputStream) throws IOException {
        List<Position> positionList = positionRepository.findAllByIsDeletedFalseAndActiveTrue();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("position" + LocalDate.now());
        String[] headers = {
                "code",
                "name",
                "description",
                "active"
        };
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowIndex = 1;

        for (var position : positionList) {
            Row row = sheet.createRow(rowIndex++);
            PositionExcelDto dto = mapper.toDto(position);
            buildRow(row, dto);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    public List<PositionExcelDto> parseExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            return buildToDto(sheet);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
    }

    private List<PositionExcelDto> buildToDto(Sheet sheet) {
        List<PositionExcelDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            PositionExcelDto dto = new PositionExcelDto();
            dto.setCode(excelHelper.getString(row.getCell(0)));
            dto.setName(excelHelper.getString(row.getCell(1)));
            dto.setDescription(excelHelper.getString(row.getCell(2)));
            // Handle boolean value from Excel
            Cell activeCell = row.getCell(3);
            if (activeCell != null) {
                if (activeCell.getCellType() == CellType.BOOLEAN) {
                    dto.setActive(activeCell.getBooleanCellValue());
                } else {
                    String activeStr = excelHelper.getString(activeCell);
                    dto.setActive(activeStr != null && Boolean.parseBoolean(activeStr));
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }

    public void buildRow(Row row, PositionExcelDto dto) {
        row.createCell(0).setCellValue(dto.getCode());
        row.createCell(1).setCellValue(dto.getName());
        row.createCell(2).setCellValue(dto.getDescription());
        row.createCell(3).setCellValue(dto.getActive() != null ? dto.getActive() : false);
    }
}
