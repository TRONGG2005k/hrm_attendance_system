package com.example.hrm.modules.contract.mapper;

import com.example.hrm.modules.contract.dto.response.AllowanceRuleResponse;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.organization.mapper.PositionMapper;
import com.example.hrm.modules.employee.mapper.SubDepartmentMapper;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
                AllowanceMapper.class,
                PositionMapper.class,
                SubDepartmentMapper.class
        }
)
public interface AllowanceRuleMapper {

    AllowanceRuleResponse toResponse(AllowanceRule entity);
}
