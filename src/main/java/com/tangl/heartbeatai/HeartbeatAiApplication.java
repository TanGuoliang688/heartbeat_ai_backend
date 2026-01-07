package com.tangl.heartbeatai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tangl.heartbeatai.mapper")
public class HeartbeatAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeartbeatAiApplication.class, args);
    }

}
