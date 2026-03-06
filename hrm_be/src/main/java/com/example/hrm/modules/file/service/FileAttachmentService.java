package com.example.hrm.modules.file.service;

import com.example.hrm.modules.file.dto.request.FileUploadRequest;
import com.example.hrm.modules.file.dto.response.UploadedFileResult;
import com.example.hrm.modules.file.entity.FileAttachment;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.file.repository.FileAttachmentRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Data
public class FileAttachmentService {

        private final FileUploadService fileUploadService;
        private final FileAttachmentRepository fileAttachmentRepository;

        /**
         * Upload file và lưu thông tin vào database
         * 
         * @param file        MultipartFile
         * @param refType     loại thực thể (ví dụ: "EMPLOYEE")
         * @param refId       id của thực thể liên kết
         * @param description mô tả file (optional)
         * @return FileAttachment vừa lưu
         */
        @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
        public FileAttachment uploadFile(MultipartFile file, String refType, String refId, String description) {
                // 1. Lưu file lên filesystem
                String savedFileName = fileUploadService.uploadFile(file);

                // 2. Tạo FileAttachment entity
                FileAttachment attachment = FileAttachment.builder()
                                .fileName(file.getOriginalFilename())
                                .fileType(file.getContentType())
                                .fileSize(file.getSize())
                                .fileUrl(savedFileName)
                                .description(description)
                                .refType(refType)
                                .refId(refId)
                                .createdAt(LocalDateTime.now())
                                .isDeleted(false)
                                .build();

                // 3. Lưu vào DB
                return fileAttachmentRepository.save(attachment);
        }

        @PreAuthorize("hasAnyRole('MANAGER', 'HR_STAFF', 'HR_MANAGER', 'ADMIN')")
        public FileAttachment uploadFile(FileUploadRequest request) {

                validateDuplicate(
                                request.getRefType(),
                                request.getRefId(),
                                request.getFilename());

                UploadedFileResult result = fileUploadService.uploadFile(
                                request.getFilename(),
                                request.getContentType(),
                                request.getInputStream(),
                                request.getSize());

                return fileAttachmentRepository.save(
                                FileAttachment.builder()
                                                .fileName(result.getOriginalFilename())
                                                .fileType(result.getContentType())
                                                .fileSize(result.getSize())
                                                .fileUrl(result.getUniqueFileName())
                                                .description(request.getDescription())
                                                .refType(request.getRefType())
                                                .refId(request.getRefId())
                                                .category(request.getCategory())
                                                .createdAt(LocalDateTime.now())
                                                .isDeleted(false)
                                                .build());
        }

        /**
         * Xóa file cả trên filesystem và DB
         */
        @PreAuthorize("hasAnyRole('HR_STAFF', 'HR_MANAGER', 'ADMIN')")
        public void deleteFile(String fileId) {
                FileAttachment attachment = fileAttachmentRepository.findById(fileId)
                                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND, 404,
                                                "File không tồn tại"));

                // Xóa file thực tế
                fileUploadService.deleteFile(attachment.getFileUrl());

                // Xóa trong DB
                attachment.setIsDeleted(true);
                attachment.setDeletedAt(LocalDateTime.now());
                fileAttachmentRepository.save(attachment);
        }

        private void validateDuplicate(String refType, String refId, String fileName) {

                boolean exists = fileAttachmentRepository
                                .existsByRefTypeAndRefIdAndFileNameAndIsDeletedFalse(
                                                refType,
                                                refId,
                                                fileName);

                if (exists) {

                        throw new AppException(
                                        ErrorCode.FILE_ALREADY_EXISTS,
                                        400,
                                        "File đã tồn tại: " + fileName);
                }
        }

}
