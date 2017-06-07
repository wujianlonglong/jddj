package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wujianlong on 2017/6/2.
 */
@Data
public class MarktPrice {

    private String goodCode;

    private BigDecimal marktPrice;
}
