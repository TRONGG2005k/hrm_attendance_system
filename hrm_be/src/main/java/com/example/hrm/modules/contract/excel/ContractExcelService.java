package com.example.hrm.modules.contract.excel;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.excel.dto.ContractExcelDto;
import com.example.hrm.modules.contract.excel.mapper.ContractExcelMapper;
import com.example.hrm.modules.contract.excel.validator.ContractExcelValidator;
import com.example.hrm.modules.contract.service.ContractService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import java.io.ByteArrayOutputStream;
import com.example.hrm.shared.ExcelResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractExcelService {
    private final ContractExcelMapper mapper;
    private final ContractExcelValidator validator;
    private final ContractService contractService;

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ExcelResult importFile(MultipartFile file){
        List<ContractExcelDto> dtos = parseExcel(file);
        int successCount = 0;
        int rowNumber = 2; // nếu dòng 1 là header
        List<String> errors = new ArrayList<>();
        for (var dto : dtos){
            List<String> rowErrors = validator.validateContract(dto, rowNumber);
            if (!rowErrors.isEmpty()) {
                errors.addAll(rowErrors);
                rowNumber++;
                continue;
            }

            ContractRequest request = mapper.toRequest(dto);
            contractService.create(request);

            successCount++;
            rowNumber++;
        }
        return new ExcelResult(successCount, errors);
    }

    @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
    public ByteArrayResource exportFile() {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Contracts");

            // ================= HEADER =================
            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("employee_code");
            header.createCell(1).setCellValue("contract_code");
            header.createCell(2).setCellValue("contract_type");
            header.createCell(3).setCellValue("sign_date");
            header.createCell(4).setCellValue("start_date");
            header.createCell(5).setCellValue("end_date");
            header.createCell(6).setCellValue("contract_status");
            header.createCell(7).setCellValue("base_salary");
            header.createCell(8).setCellValue("salary_coefficient");
            header.createCell(9).setCellValue("salary_effective_date");
            header.createCell(10).setCellValue("salary_status");
            header.createCell(11).setCellValue("note");

            // ================= DATA =================

            var contracts = contractService.getAllForExcel();
            // bạn cần viết hàm này

            int rowIndex = 1;

            for (var contract : contracts) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(contract.getEmployeeCode());
                row.createCell(1).setCellValue(contract.getContractCode());
                row.createCell(2).setCellValue(contract.getContractType().name());

                row.createCell(3).setCellValue(
                        contract.getSignDate() != null ?
                                contract.getSignDate().toString() : ""
                );

                row.createCell(4).setCellValue(
                        contract.getStartDate() != null ?
                                contract.getStartDate().toString() : ""
                );

                row.createCell(5).setCellValue(
                        contract.getEndDate() != null ?
                                contract.getEndDate().toString() : ""
                );

                row.createCell(6).setCellValue(contract.getContractStatus().name());

                row.createCell(7).setCellValue(
                        contract.getBaseSalary() != null ?
                                contract.getBaseSalary().doubleValue() : 0
                );

                row.createCell(8).setCellValue(
                        contract.getSalaryCoefficient() != null ?
                                contract.getSalaryCoefficient() : 0
                );

                row.createCell(9).setCellValue(
                        contract.getSalaryEffectiveDate() != null ?
                                contract.getSalaryEffectiveDate().toString() : ""
                );

                row.createCell(10).setCellValue(
                        contract.getSalaryStatus() != null ?
                                contract.getSalaryStatus().name() : ""
                );

                row.createCell(11).setCellValue(
                        contract.getNote() != null ?
                                contract.getNote() : ""
                );
            }

            // auto size column
            for (int i = 0; i < 12; i++) {
                sheet.autoSizeColumn(i);
            }

            // write to memory
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {

            log.error("Export Excel error", e);

            throw new RuntimeException("Export Excel failed", e);
        }
    }

    private  List<ContractExcelDto> parseExcel(MultipartFile file) {
        List<ContractExcelDto> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            dtos.addAll(mapper.toDto(sheet));

        } catch (Exception e) {
            log.error("Lỗi đọc file Excel: {}", e.getMessage());
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return dtos;
    }

}
