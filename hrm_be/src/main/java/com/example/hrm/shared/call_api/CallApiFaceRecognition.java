package com.example.hrm.shared.call_api;

import com.example.hrm.modules.face_recognition.dto.response.FaceRecognizeResponse;
import com.example.hrm.shared.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CallApiFaceRecognition {

    private final RestTemplate restTemplate;

    @Value("${face-recognition.base-url:http://127.0.0.1:8000}")
    private String baseUrl;

    @Value("${face-recognition.endpoints.register:/facial-recognition/register-face}")
    private String registerEndpoint;

    @Value("${face-recognition.endpoints.recognize:/facial-recognition/face-recognition}")
    private String recognizeEndpoint;

    @Value("${face-recognition.endpoints.update:/facial-recognition/update-face}")
    private String updateEndpoint;

    @Value("${face-recognition.endpoints.delete:/facial-recognition/delete-face}")
    private String deleteEndpoint;

    @Value("${face-recognition.endpoints.register-batch:/facial-recognition/register-face-batch}")
    private String registerBatchEndpoint;

    public CallApiFaceRecognition(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getRegisterApi() {
        return baseUrl + registerEndpoint;
    }

    private String getRecognizeApi() {
        return baseUrl + recognizeEndpoint;
    }

    private String getUpdateApi() {
        return baseUrl + updateEndpoint;
    }

    private String getDeleteApi() {
        return baseUrl + deleteEndpoint;
    }

    private String getRegisterBatchApi() {
        return baseUrl + registerBatchEndpoint;
    }

    // ================= REGISTER BATCH =================
    public Map<String, Object> registerFaceBatch(byte[] zipBytes, String filename) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // tạo resource từ byte[]
        ByteArrayResource resource = new ByteArrayResource(zipBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.exchange(
                            getRegisterBatchApi(),
                            HttpMethod.POST,
                            request,
                            Map.class
                    );

            return response.getBody();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Gọi API register-face-batch thất bại: " + e.getMessage()
            );
        }
    }

    // ================= REGISTER =================
    public String registerFace(String employeeId, List<String> imagesBase64) {
        Map<String, Object> requestBody = Map.of(
                "employee_id", employeeId,
                "images", imagesBase64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MessageResponse> response =
                    restTemplate.postForEntity(getRegisterApi(), request, MessageResponse.class);

            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API register-face thất bại: " + e.getMessage());
        }
    }

    // ================= RECOGNIZE =================
    public FaceRecognizeResponse recognizeFace(String imageBase64) {
        Map<String, Object> requestBody = Map.of(
                "image", imageBase64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<FaceRecognizeResponse> response =
                    restTemplate.postForEntity(getRecognizeApi(), request, FaceRecognizeResponse.class);
            log.warn("response {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API face-recognition thất bại: " + e.getMessage());
        }
    }

    // ================= UPDATE =================
    public String updateFace(String employeeId, List<String> newImagesBase64) {
        Map<String, Object> requestBody = Map.of(
                "employee_id", employeeId,
                "new_images", newImagesBase64
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MessageResponse> response =
                    restTemplate.exchange(
                            getUpdateApi(),
                            HttpMethod.PUT,
                            request,
                            MessageResponse.class
                    );

            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API update-face thất bại: " + e.getMessage());
        }
    }

    // ================= DELETE =================
    public String deleteFace(String employeeId) {
        Map<String, Object> requestBody = Map.of(
                "employee_id", employeeId
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MessageResponse> response =
                    restTemplate.exchange(
                            getDeleteApi(),
                            HttpMethod.DELETE,
                            request,
                            MessageResponse.class
                    );


            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (Exception e) {
            throw new RuntimeException("Gọi API delete-face thất bại: " + e.getMessage());
        }
    }
}
