package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/5.
 */
@Data
public class DtChangePriceRequest {
    private String skuId ;

    private String stationNo ;

    private String price ;

    private String marketPrice ;
}
