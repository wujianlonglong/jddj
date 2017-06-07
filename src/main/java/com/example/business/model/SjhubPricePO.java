package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wujianlong on 2016/10/31.
 */
@Data
public class SjhubPricePO {

    private String scbh;

    private String glbh;

    private String sj_goods_code;

    private BigDecimal jzsj;

    private BigDecimal jzhyj;
}
