package com.example.hrm.modules.file.service;

import com.example.hrm.shared.enums.FileCategory;
import com.example.hrm.shared.mapper.EnumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class ZipExtractionService {
    private final EnumMapper enumMapper;

    public void extractAndProcess(
            MultipartFile zipFile,
            ZipEntryProcessor processor) throws IOException {

        try (ZipInputStream zis =
                     new ZipInputStream(
                             new BufferedInputStream(zipFile.getInputStream()))) {

                ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    continue;
                }

                String entryName = entry.getName();

                if (isInvalidEntry(entryName)) {
                    continue;
                }

                String[] parts = entryName.split("/");

                if (parts.length < 3) {
                    continue; // hoặc throw error
                }

                String employeeCode = parts[0];
                FileCategory category = enumMapper.mapFileCategory(parts[1])    ;
                String fileName = parts[2];

                processor.process(
                        employeeCode,
                        category,
                        fileName,
                        zis,
                        detectContentType(fileName),
                        entry.getSize()
                );

                zis.closeEntry();
            }

        }
    }

    private boolean isInvalidEntry(String filename) {
        return filename.contains("..")
                || filename.contains("\\")
                || filename.startsWith("/");
    }

    private String detectContentType(String fileName) {

        String ext = getFileExtension(fileName).toLowerCase();

        return switch (ext) {

            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";

            case "pdf" -> "application/pdf";

            case "doc" -> "application/msword";
            case "docx" ->
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" ->
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" ->
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation";

            default -> "application/octet-stream";
        };
    }

    public String getFileExtension(String fileName) {

        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // loại bỏ path nếu có (zip entry có thể chứa folder)
        int lastSeparator = Math.max(
                fileName.lastIndexOf('/'),
                fileName.lastIndexOf('\\')
        );

        String name = (lastSeparator >= 0)
                ? fileName.substring(lastSeparator + 1)
                : fileName;

        int lastDot = name.lastIndexOf('.');

        // không có extension hoặc file dạng ".gitignore"
        if (lastDot <= 0 || lastDot == name.length() - 1) {
            return "";
        }

        return name.substring(lastDot + 1).toLowerCase();
    }


}

