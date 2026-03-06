package com.example.hrm.modules.file.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileAttachmentRequest {

    String fileName;

    String fileType;

    Long fileSize;

    String description;

    @NotBlank(message = "Loại đối tượng liên kết không được để trống")
    String refType;

    @NotBlank(message = "ID đối tượng liên kết không được để trống")
    String refId;
}
