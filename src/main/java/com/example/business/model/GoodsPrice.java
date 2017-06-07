package com.example.business.model;

/**
 * Created by wujianlong on 2017/6/2.
 */


import lombok.Data;

import java.math.BigDecimal;


@Data
public class GoodsPrice {

    /**
     * 三江门店id
     */
    private String shopCode;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 零售价
     */
    private BigDecimal salePrice;

    /**
     * 会员价
     */
    private BigDecimal memberPrice;


}
