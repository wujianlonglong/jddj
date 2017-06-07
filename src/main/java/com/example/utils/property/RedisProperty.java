package com.example.utils.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wujianlong on 2017/3/10.
 */
@Component
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisProperty {

    private String database;


    private String host;
}
