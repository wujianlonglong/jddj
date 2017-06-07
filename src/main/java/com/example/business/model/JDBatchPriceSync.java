package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wujianlong on 2016/11/3.
 */
@Data
public class JDBatchPriceSync {

    private String batchno;
    private BigDecimal batchsaleprice;
    private BigDecimal batchmemberprice;
    private BigDecimal batchmarketprice;
    private String storeid;
    private BigDecimal goodsid;
    private Date syncTime;
}
