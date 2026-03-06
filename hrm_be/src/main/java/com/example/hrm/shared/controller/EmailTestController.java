package com.example.hrm.shared.controller;

import com.example.hrm.shared.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev/mail")
@RequiredArgsConstructor
@Profile("dev") // RẤT QUAN TRỌNG
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/test")
    public String testMail(@RequestParam String to) {
        emailService.sendTestMail(to);
        return "Mail sent to " + to;
    }
}
