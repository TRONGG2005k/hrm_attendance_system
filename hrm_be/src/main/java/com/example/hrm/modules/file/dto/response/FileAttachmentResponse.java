package com.example.hrm.modules.file.dto.response;

import com.example.hrm.shared.enums.FileCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileAttachmentResponse {

    String id;

    String fileName;

    String fileType;

    String fileUrl;

    Long fileSize;

    String description;

    String refType;

    String refId;

    LocalDateTime createdAt;

    FileCategory category;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
