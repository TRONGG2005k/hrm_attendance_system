package com.example.hrm.shared.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class ExcelImportException extends RuntimeException {

    private final List<String> errors;

    public ExcelImportException(List<String> errors) {
        super("Import Excel thất bại");
        this.errors = errors;
    }

}
