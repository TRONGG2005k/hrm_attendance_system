package com.example.hrm.modules.file.mapper;

import com.example.hrm.modules.file.dto.request.FileAttachmentRequest;
import com.example.hrm.modules.file.dto.response.FileAttachmentResponse;
import com.example.hrm.modules.file.entity.FileAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FileAttachmentMapper {

    FileAttachmentMapper INSTANCE = Mappers.getMapper(FileAttachmentMapper.class);

    /**
     * Map từ FileAttachment entity sang FileAttachmentResponse
     */
    FileAttachmentResponse toResponse(FileAttachment entity);

    /**
     * Map từ FileAttachmentRequest sang FileAttachment entity
     * Lưu ý: các field hệ thống như createdAt, isDeleted sẽ set riêng, không map từ request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    FileAttachment toEntity(FileAttachmentRequest request);
}
