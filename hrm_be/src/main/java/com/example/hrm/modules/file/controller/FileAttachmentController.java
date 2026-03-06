package com.example.hrm.modules.file.controller;

import com.example.hrm.modules.file.entity.FileAttachment;
import com.example.hrm.modules.file.service.FileAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${app.api-prefix}/files")
@RequiredArgsConstructor
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;

    /**
     * Upload file gắn với 1 entity (refType + refId)
     */
    @PostMapping("/upload")
    public ResponseEntity<FileAttachment> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("refType") String refType,
            @RequestParam("refId") String refId,
            @RequestParam(value = "description", required = false) String description
    ) {
        FileAttachment attachment = fileAttachmentService.uploadFile(file, refType, refId, description);
        return ResponseEntity.ok(attachment);
    }

    /**
     * Download file theo ID
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        FileAttachment attachment = fileAttachmentService
                .getFileAttachmentRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));

        byte[] fileData = fileAttachmentService.getFileUploadService().downloadFile(attachment.getFileUrl());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(fileData);
    }

    /**
     * Xóa file
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable String id) {
        fileAttachmentService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
