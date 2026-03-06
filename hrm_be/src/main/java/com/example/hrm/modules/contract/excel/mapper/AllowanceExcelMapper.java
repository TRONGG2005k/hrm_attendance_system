package com.example.hrm.modules.contract.excel.mapper;

import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.request.AllowanceRuleRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.excel.dto.AllowanceExcelDTO;
import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.entity.SubDepartment;
import com.example.hrm.modules.organization.repository.PositionRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllowanceExcelMapper {
    private final ExcelHelper excelHelper;
    private final PositionRepository positionRepository;
    private final SubDepartmentRepository subDepartmentRepository;

    public List<AllowanceExcelDTO> toDto(Sheet sheet) {

        List<AllowanceExcelDTO> list = new ArrayList<>();

        // bỏ qua header (row 0)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null)
                continue;

            AllowanceExcelDTO dto = new AllowanceExcelDTO();

            dto.setCode(excelHelper.getString(row.getCell(0)));

            dto.setName(excelHelper.getString(row.getCell(1)));

            dto.setDescription(excelHelper.getString(row.getCell(2)));

            dto.setActive(excelHelper.getBoolean(row.getCell(3)));

            String positionCode = excelHelper.getString(row.getCell(4));
            String subDepartmentCode = excelHelper.getString(row.getCell(5));

            dto.setPositionCode(positionCode);

            dto.setSubDepartmentName(subDepartmentCode);

            dto.setAmount(excelHelper.getBigDecimal(row.getCell(6)));

            dto.setCalculationType(excelHelper.getAllowanceCalculationType(row.getCell(7)));

            log.warn("getAllowanceCalculationType: {}", excelHelper.getAllowanceCalculationType(row.getCell(7)));
            // query luôn và set entity
            Position position = positionRepository
                    .findByCodeAndIsDeletedFalse(positionCode)
                    .orElse(null);

            SubDepartment subDepartment = subDepartmentRepository
                    .findByNameAndIsDeletedFalse(subDepartmentCode)
                    .orElse(null);

            dto.setPosition(position);
            dto.setSubDepartment(subDepartment);
            list.add(dto);
        }

        return list;
    }

    public AllowanceRequest toAllowanceRequest(AllowanceExcelDTO dto) {
        AllowanceRequest allowance = new AllowanceRequest();
        allowance.setCode(dto.getCode());
        allowance.setName(dto.getName());
        allowance.setDescription(dto.getDescription());
        return allowance;
    }

    public AllowanceRuleRequest toAllowanceRuleRequest(AllowanceExcelDTO dto, AllowanceResponse allowance) {
        var p = positionRepository.findByCodeAndIsDeletedFalse(dto.getPositionCode())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 4040,
                        "Không tồn tại chức vụ: " + dto.getPositionCode()));
        var s = subDepartmentRepository.findByNameAndIsDeletedFalse(dto.getSubDepartmentName())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 4040,
                        "Không tồn tại phòng ban: " + dto.getSubDepartmentName()));
        AllowanceRuleRequest allowanceRule = new AllowanceRuleRequest();
        allowanceRule.setAmount(dto.getAmount());
        allowanceRule.setAllowanceId(allowance.getId());
        allowanceRule.setCalculationType(dto.getCalculationType());
        allowanceRule.setPositionId(p.getId());
        allowanceRule.setSubDepartmentId(s.getId());
        return allowanceRule;
    }
}
