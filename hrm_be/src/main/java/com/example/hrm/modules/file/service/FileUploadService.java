package com.example.hrm.modules.file.service;

import com.example.hrm.modules.file.dto.response.UploadedFileResult;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:52428800}")
    private long maxFileSize;

    private static final String[] ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx"
    };

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    );

    private static final byte[][] FILE_SIGNATURES = {
            {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, // JPEG
            {(byte) 0x89, 0x50, 0x4E, 0x47}, // PNG
            {0x47, 0x49, 0x46, 0x38}, // GIF
            {(byte) 0x25, 0x50, 0x44, 0x46}, // PDF
            {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0}, // DOC, XLS, PPT (old Office)
            {0x50, 0x4B, 0x03, 0x04}  // DOCX, XLSX, PPTX (new Office - ZIP based)
    };


    public UploadedFileResult uploadFile(
            String originalFileName,
            String contentType,
            InputStream inputStream,
            long size
    ) {

        if (inputStream == null || originalFileName == null || originalFileName.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 400, "File không hợp lệ");
        }

        if (size > maxFileSize) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED, 400,
                    "Kích thước file vượt quá giới hạn");
        }

        if (!isAllowedFileType(originalFileName)) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE, 400,
                    "Loại file không được hỗ trợ");
        }

        try {

            BufferedInputStream bis = new BufferedInputStream(inputStream);

            bis.mark(32);


            if (!isValidFileSignature(bis)) {
                throw new AppException(ErrorCode.FILE_INVALID_TYPE, 400,
                        "File signature không hợp lệ");
            }

            bis.reset();

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = generateUniqueFileName(originalFileName);

            Path filePath = uploadPath.resolve(uniqueFileName);

            long actualSize = Files.copy(
                    bis,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            if (actualSize > maxFileSize) {

                // xóa file vừa copy vì nó invalid
                Files.deleteIfExists(filePath);

                throw new AppException(
                        ErrorCode.FILE_SIZE_EXCEEDED,
                        400,
                        "Kích thước file vượt quá giới hạn: "
                                + (maxFileSize / 1024 / 1024) + "MB"
                );
            }

            // trả về result object
            return new UploadedFileResult(
                    originalFileName,
                    contentType,
                    size,
                    uniqueFileName
            );

        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500,
                    "Lỗi khi lưu file: " + e.getMessage());
        }
    }


    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 400, "File không được để trống");
        }

        // Kiểm tra kích thước file
        if (file.getSize() > maxFileSize) {
            throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED, 400,
                    "Kích thước file vượt quá giới hạn: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // Kiểm tra định dạng file
        String fileName = file.getOriginalFilename();
        if (fileName == null || !isAllowedFileType(fileName)) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE, 400, "Loại file không được hỗ trợ");
        }

        // Kiểm tra MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE, 400, "MIME type không được hỗ trợ");
        }

        // Kiểm tra file signature (magic number)
        if (!isValidFileSignature(file)) {
            throw new AppException(ErrorCode.FILE_INVALID_TYPE, 400, "Nội dung file không hợp lệ");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file duy nhất
            String uniqueFileName = generateUniqueFileName(fileName);
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Lưu file
            Files.write(filePath, file.getBytes());

            return uniqueFileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500, "Lỗi khi lưu file: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500, "Lỗi khi xóa file: " + e.getMessage());
        }
    }

    public byte[] downloadFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND, 404, "Tên file không hợp lệ");
        }

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(ErrorCode.FILE_NOT_FOUND, 404, "File không tìm thấy");
            }

            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, 500, "Lỗi khi đọc file: " + e.getMessage());
        }
    }

    private boolean isAllowedFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "";
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    private boolean isValidFileSignature(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int bytesRead = is.read(header);
            if (bytesRead < 4) {
                return false;
            }

            for (byte[] signature : FILE_SIGNATURES) {
                if (headerMatches(header, signature)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isValidFileSignature(InputStream is) throws IOException {

        byte[] header = new byte[4];

        int bytesRead = is.read(header);

        if (bytesRead < 4) {
            return false;
        }

        for (byte[] signature : FILE_SIGNATURES) {
            if (headerMatches(header, signature)) {
                return true;
            }
        }

        return false;
    }

    private boolean headerMatches(byte[] header, byte[] signature) {
        for (int i = 0; i < signature.length; i++) {
            if (header[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
