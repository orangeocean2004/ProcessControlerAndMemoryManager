package com.os;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OsApplication.class, args);
        System.out.println("=== 进程调度模拟器已启动 ===");
        System.out.println("请在浏览器中访问 http://localhost:8080");
    }
}