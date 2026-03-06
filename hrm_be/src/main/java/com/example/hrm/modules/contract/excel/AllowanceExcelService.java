package com.example.hrm.modules.contract.excel;

import com.example.hrm.modules.contract.controller.AllowanceRuleService;
import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.request.AllowanceRuleRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.contract.excel.dto.AllowanceExcelDTO;
import com.example.hrm.modules.contract.excel.mapper.AllowanceExcelMapper;
import com.example.hrm.modules.contract.excel.validator.AllowanceExcelValidator;
import com.example.hrm.modules.contract.service.AllowanceService;
import com.example.hrm.shared.ExcelResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllowanceExcelService {

    private final AllowanceExcelMapper mapper;
    private final AllowanceExcelValidator validator;
    private final AllowanceService allowanceService;
    private final AllowanceRuleService allowanceRuleService;

    public ExcelResult importFile(MultipartFile file) {
        List<AllowanceExcelDTO> dtos = parseExcel(file);
        int successCount = 0;
        int rowNumber = 2; // nếu dòng 1 là header
        Set<String> codeSet = new HashSet<>();
        List<String> errors = new ArrayList<>();
        for (var dto : dtos) {
            if (!codeSet.add(dto.getCode())) {

                errors.add("Dòng " + rowNumber + ": Code bị trùng trong file");

                continue;
            }

            List<String> rowErrors = validator.validate(dto, rowNumber);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            AllowanceRequest request = mapper.toAllowanceRequest(dto);

            AllowanceResponse allowance = allowanceService.create(request);
            AllowanceRuleRequest allowanceRuleRequest = mapper.toAllowanceRuleRequest(dto, allowance);
            allowanceRuleService.create(allowanceRuleRequest);
            successCount++;
            rowNumber++;
        }
        return new ExcelResult(successCount, errors);
    }

    private List<AllowanceExcelDTO> parseExcel(MultipartFile file) {
        List<AllowanceExcelDTO> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            dtos.addAll(mapper.toDto(sheet));

        } catch (Exception e) {
            log.error("Lỗi đọc file Excel: {}", e.getMessage());
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return dtos;
    }

    public ByteArrayInputStream exportData() {

        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Allowance");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Header
            String[] headers = {
                    "code",
                    "name",
                    "description",
                    "active",
                    "positionCode",
                    "subDepartmentName",
                    "amount",
                    "calculationType"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // LẤY DATA TỪ DB
            List<AllowanceRule> allowances = allowanceRuleService.getAll(); // bạn cần viết hàm này

            int rowIdx = 1;

            for (var allowance : allowances) {

                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(allowance.getAllowance().getCode());
                row.createCell(1).setCellValue(allowance.getAllowance().getName());
                row.createCell(2).setCellValue(
                        allowance.getAllowance().getDescription() == null ? ""
                                : allowance.getAllowance().getDescription());
                row.createCell(3).setCellValue(allowance.getActive());

                row.createCell(4).setCellValue(
                        allowance.getPosition() != null ? allowance.getPosition().getCode() : "");

                row.createCell(5).setCellValue(
                        allowance.getSubDepartment() != null ? allowance.getSubDepartment().getName() : "");

                row.createCell(6).setCellValue(
                        allowance.getAmount().doubleValue());

                row.createCell(7).setCellValue(
                        allowance.getCalculationType().name());
            }

            // autosize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Export failed", e);
        }
    }

}
