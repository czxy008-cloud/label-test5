package com.meeting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.meeting.mapper")
public class MeetingBookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeetingBookingApplication.class, args);
        System.out.println("\n======================================");
        System.out.println("  会议室预订系统启动成功!");
        System.out.println("  接口地址: http://localhost:8080/api");
        System.out.println("======================================\n");
    }
}
