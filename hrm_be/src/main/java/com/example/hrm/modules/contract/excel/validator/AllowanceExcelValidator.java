package com.example.hrm.modules.contract.excel.validator;

import com.example.hrm.modules.contract.excel.dto.AllowanceExcelDTO;
import com.example.hrm.modules.contract.repository.AllowanceRepository;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.shared.excel.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AllowanceExcelValidator {
    private final AllowanceRepository allowanceRepository;
    private final AllowanceRuleRepository allowanceRuleRepository;
    private final ExcelHelper excelHelper;

    public List<String> validate(AllowanceExcelDTO dto, long rowNumber) {

        List<String> err = new ArrayList<>();

        if (excelHelper.isBlank(dto.getCode())) {
            err.add("Dòng " + rowNumber + ": Code không được để trống");
        } else if (allowanceRepository.existsByCodeAndActiveTrue(dto.getCode())) {
            err.add("Dòng " + rowNumber + ": Code đã tồn tại");
        }

        if (excelHelper.isBlank(dto.getName())) {
            err.add("Dòng " + rowNumber + ": Name không được để trống");
        }

        // validate bằng entity (chuẩn)
        if (dto.getPosition() == null) {
            err.add("Dòng " + rowNumber + ": Position không tồn tại với code: " + dto.getPositionCode());
        }

        if (dto.getSubDepartment() == null) {
            err.add("Dòng " + rowNumber + ": SubDepartment không tồn tại với name: " + dto.getSubDepartmentName());
        }
        if (dto.getPosition() != null && dto.getSubDepartment() != null) {

            boolean exists = allowanceRuleRepository
                    .existsByAllowanceCodeAndPositionIdAndSubDepartmentIdAndActiveTrue(
                            dto.getCode(),
                            dto.getPosition().getId(),
                            dto.getSubDepartment().getId());

            if (exists) {
                err.add("Dòng " + rowNumber + ": Rule đã tồn tại với Position và SubDepartment này");
            }
        }

        if (dto.getCalculationType() == null) {
            err.add("Dòng " + rowNumber + ": CalculationType không hợp lệ");
        }

        if (dto.getAmount() == null) {
            err.add("Dòng " + rowNumber + ": Amount không hợp lệ");
        }

        return err;
    }

}
