package com.example.business.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wujianlong on 2016/10/27.
 */

@Data
public class JDGoodsPriceItemObj implements Serializable {

    /**
     * 京东sku
     */
    private String skuId ;



    /**
     *京东门店
     */
    private String stationNo ;

     /**
     * 门店价
     */
    private long price ;

    /**
     * 市场价
     */
    private long marketPrice ;

}


