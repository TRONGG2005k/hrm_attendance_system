package com.example.hrm.modules.face_recognition.controller;

import com.example.hrm.modules.face_recognition.dto.request.FaceUploadRequest;
import com.example.hrm.modules.face_recognition.service.FaceRecognitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/employees")
@RequiredArgsConstructor
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;

    // ================= REGISTER =================
    @PostMapping("/{employeeId}/faces")
    public ResponseEntity<?> registerFace(
            @PathVariable String employeeId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        FaceUploadRequest request = new FaceUploadRequest();
        request.setEmployeeId(employeeId);
        request.setFiles(files);

        return ResponseEntity.ok(
                faceRecognitionService.registerFace(request)
        );
    }

    // ================= RECOGNIZE =================
    @PostMapping("/faces/recognize")
    public ResponseEntity<?> recognizeFace(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                faceRecognitionService.recognizeFace(file)
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{employeeId}/faces")
    public ResponseEntity<?> updateFace(
            @PathVariable String employeeId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        FaceUploadRequest request = new FaceUploadRequest();
        request.setEmployeeId(employeeId);
        request.setFiles(files);

        return ResponseEntity.ok(
                faceRecognitionService.updateFace(request)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{employeeId}/faces")
    public ResponseEntity<?> deleteFace(
            @PathVariable String employeeId
    ) {
        return ResponseEntity.ok(
                faceRecognitionService.deleteFace(employeeId)
        );
    }

    // ================= REGISTER BATCH =================
    @PostMapping("/faces/batch")
    public ResponseEntity<?> registerFaceBatch(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                faceRecognitionService.registerFaceBatch(file)
        );
    }

}
