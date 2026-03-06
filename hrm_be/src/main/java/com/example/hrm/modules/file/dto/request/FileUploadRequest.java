package com.example.hrm.modules.file.dto.request;

import com.example.hrm.shared.enums.FileCategory;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class FileUploadRequest {

    private final String filename;

    private final InputStream inputStream;

    private final String contentType;

    private final long size;

    private final String refType;

    private final String refId;

    private final FileCategory category;

    private final String description;

}
