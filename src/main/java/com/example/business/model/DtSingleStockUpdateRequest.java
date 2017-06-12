package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/12.
 */
@Data
public class DtSingleStockUpdateRequest {
    public String skuId ;

    public String stationNo ;

    public String currentQty ;
}
