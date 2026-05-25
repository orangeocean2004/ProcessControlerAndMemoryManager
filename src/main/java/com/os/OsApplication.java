package com.os;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OsApplication.class, args);
        System.out.println("=== 进程调度模拟器已启动 ===");
        System.out.println("请在浏览器中访问 http://localhost:8080");
        System.out.println("使用 POST /api/process 创建进程，GET /api/state 查看状态，POST /api/strategy 切换算法");
    }
}