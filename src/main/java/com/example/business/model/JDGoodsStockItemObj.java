package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/12/7.
 */

@Data
public class JDGoodsStockItemObj {
    private long skuId ;

    private String stationNo ;

    private Integer currentQty ;
}
