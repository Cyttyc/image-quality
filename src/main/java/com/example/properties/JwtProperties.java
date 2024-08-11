package com.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "example.jwt")
@Component
public class JwtProperties {
    //受试者登录生成jwt令牌相关配置
    private String subjectSecretKey;
    private long subjectTtl;
    private String subjectTokenName;
}
