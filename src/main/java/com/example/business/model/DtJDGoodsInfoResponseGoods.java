package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * Created by wujianlong on 2017/6/15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtJDGoodsInfoResponseGoods {
    private String outSkuId ;

    private String skuId ;

    private String skuName ;

    private String upcCode ;

    private String slogan ;

    private Integer fixedStatus ;

    private String payType ;

    public DtJDGoodsInfoResponseGoods()
    {
        skuName = "";
    }
}
