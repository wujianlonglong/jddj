package com.example.utils.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by gaoqichao on 16-7-15.
 */
@Component
@Data
@ConfigurationProperties(prefix = "platform")
public class PlatformProperty {
    /**
     * 百度外卖
     */
    private String bdwm;

    /**
     * 京东到家
     */
    private String jddj;
}
