package com.example.hrm.modules.organization.excel.mapper;

import com.example.hrm.modules.organization.entity.Position;
import com.example.hrm.modules.organization.excel.dto.PositionExcelDto;
import org.springframework.stereotype.Component;

@Component
public class PositionExcelMapper {

    public Position toPosition(PositionExcelDto dto) {
        Position position = new Position();
        position.setCode(dto.getCode());
        position.setName(dto.getName());
        position.setDescription(dto.getDescription());
        position.setActive(dto.getActive() != null ? dto.getActive() : true);
        position.setIsDeleted(false);
        return position;
    }

    public PositionExcelDto toDto(Position position) {
        PositionExcelDto dto = new PositionExcelDto();
        dto.setCode(position.getCode());
        dto.setName(position.getName());
        dto.setDescription(position.getDescription());
        dto.setActive(position.getActive());
        return dto;
    }
}
