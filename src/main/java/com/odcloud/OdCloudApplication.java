package com.odcloud;

import com.odcloud.infrastructure.constant.ProfileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(ProfileConstant.class)
public class OdCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdCloudApplication.class, args);
    }

}
