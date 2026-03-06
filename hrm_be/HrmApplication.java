package com.example.hrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * HRM Application - Main Entry Point
 * 
 * Cấu hình chính cho Spring Boot application
 * 
 * ComponentScan: Quét toàn bộ modules và shared packages
 * EnableScheduling: Bật tính năng lập lịch (scheduled tasks)
 * EnableAsync: Bật tính năng async (bất đồng bộ)
 * 
 * @author HRM Team
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.hrm.modules",      // Tất cả feature modules
    "com.example.hrm.shared",       // Shared configuration & utilities
    "com.example.hrm.call_api"      // External API integrations
})
@EnableScheduling                    // Bật scheduled tasks
@EnableAsync                         // Bật async operations
public class HrmApplication {
    
    /**
     * Application entry point
     */
    public static void main(String[] args) {
        SpringApplication.run(HrmApplication.class, args);
    }
}
