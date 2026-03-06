package com.example.hrm.modules.file.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UploadedFileResult {
    private String originalFilename;
    private String contentType;
    private long size;
    private String uniqueFileName;
}
