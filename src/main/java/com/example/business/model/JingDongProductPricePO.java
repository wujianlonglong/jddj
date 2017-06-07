package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by shilhu on 2016/10/28.
 */
@Data
public class JingDongProductPricePO {

    private String storeId;
    private String goodsId;
    private BigDecimal salePrice;
    private BigDecimal memberPrice;
    private BigDecimal marketPrice;
    private int isSync;

}
