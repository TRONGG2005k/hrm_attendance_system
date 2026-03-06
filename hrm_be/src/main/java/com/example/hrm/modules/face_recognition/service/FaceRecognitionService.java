package com.example.hrm.modules.face_recognition.service;

import com.example.hrm.shared.call_api.CallApiFaceRecognition;
import com.example.hrm.modules.face_recognition.dto.request.FaceUploadRequest;
import com.example.hrm.modules.face_recognition.dto.response.FaceRecognizeResponse;
import com.example.hrm.modules.face_recognition.dto.response.RecognizeFaceResponse;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaceRecognitionService {
    private final CallApiFaceRecognition callApiFaceRecognition;
    private final EmployeeRepository employeeRepository;

    public String registerFace(FaceUploadRequest request){
        try{
            List<String> base64Images = validateAndConvertImages(request.getFiles());
            if (base64Images.isEmpty()) {
                throw new AppException(ErrorCode.HANDLING_FAILED_FILES, 409);
            }

            var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                    .orElseThrow(() ->  new AppException(ErrorCode.USER_NOT_FOUND, 404));
            return callApiFaceRecognition.registerFace(employee.getCode(), base64Images);
        } catch (Exception e){
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, 500);
        }
    }

    // ================= REGISTER BATCH =================
    public Object registerFaceBatch(MultipartFile file) {

        try {

            if (file == null || file.isEmpty()) {
                throw new AppException(ErrorCode.FILE_IS_EMPTY, 400);
            }

            // gọi API Python
            return callApiFaceRecognition.registerFaceBatch(
                    file.getBytes(),
                    file.getOriginalFilename()
            );

        } catch (IOException e) {

            log.error("Register batch failed", e);

            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, 500);
        }
    }


    public RecognizeFaceResponse recognizeFace(MultipartFile file) {
        try {

            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Ảnh gửi lên rỗng!");
            }

            // Lấy bytes từ file
            byte[] originalBytes = file.getBytes();

            // Resize (640x640)
            byte[] resized = resizeImage(originalBytes, 640, 640);

            // Chuyển sang Base64
            String base64Image = toBase64(resized);

            // Gọi API Python
            FaceRecognizeResponse response = callApiFaceRecognition.recognizeFace(base64Image);

            log.warn("{debug}{}", response);

            var employee = employeeRepository.findByCodeAndIsDeletedFalse(response.getEmployeeId())
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));


            return RecognizeFaceResponse.builder()
                    .code(employee.getCode())
                    .name(employee.getFirstName())
                    .time(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.warn("fail {}", e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, 500, "Xử lý recognizeFace thất bại: " + e.getMessage());
        }
    }

    // ================= UPDATE =================
    public String updateFace(FaceUploadRequest request) {
        try {
            List<String> base64Images = validateAndConvertImages(request.getFiles());

            var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

            return callApiFaceRecognition.updateFace(employee.getId(), base64Images);

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Update face failed", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, 500);
        }
    }

    // ================= DELETE =================
    public String deleteFace(String employeeId) {
        try {
            var employee = employeeRepository.findByIdAndIsDeletedFalse(employeeId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

            return callApiFaceRecognition.deleteFace(employee.getId());

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Delete face failed", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, 500);
        }
    }


    /*
    *
    * helper
    *
    * */


    private List<String> validateAndConvertImages(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_IS_EMPTY, 400);
        }

        List<String> base64Images = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            byte[] resized = resizeImage(file.getBytes(), 640, 640);
            base64Images.add(toBase64(resized));
        }

        if (base64Images.isEmpty()) {
            throw new AppException(ErrorCode.HANDLING_FAILED_FILES, 409);
        }

        return base64Images;
    }

    public byte[] resizeImage(byte[] originalBytes, int width, int height) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(originalBytes);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Thumbnails.of(in)
                    .size(width, height)
                    .outputFormat("jpg")
                    .toOutputStream(out);

            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Resize image failed", e);
        }
    }

    public String toBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }


}
