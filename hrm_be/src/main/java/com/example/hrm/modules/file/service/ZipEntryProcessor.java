package com.example.hrm.modules.file.service;

import com.example.hrm.shared.enums.FileCategory;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface ZipEntryProcessor {

    void process(
            String employeeCode,
            FileCategory category,
            String filename,
                 InputStream inputStream,
                 String contentType,
                 long size) throws IOException;
}
