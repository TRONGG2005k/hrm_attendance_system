package com.example.hrm.modules.file.repository;

import com.example.hrm.modules.file.entity.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, String> {
    Page<FileAttachment> findByIsDeletedFalse(Pageable pageable);
    boolean existsByRefTypeAndRefIdAndFileNameAndIsDeletedFalse(
            String refType,
            String refId,
            String fileName
    );

    List<FileAttachment> findByRefTypeAndRefIdAndIsDeletedFalse(String refType, String refId);
}
