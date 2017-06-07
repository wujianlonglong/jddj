package com.example.utils.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by wujianlong on 2016/12/7.
 */
@Component
@ConfigurationProperties(prefix = "jddj")
@Data
public class JddjProperty {


    private String v;

    private String token;

    private String app_key;

    private String app_secret;

    private String format;

    private String sign;

    private String timestamp;

    private String apiUrl;

    private String centerUrl;

    /**
     * 一次同步库存最多的商品数量
     */
    private int batchStockNum;


    /**
     * 一次同步价格最多的商品数量
     */
    private int batchPriceNum;
}
