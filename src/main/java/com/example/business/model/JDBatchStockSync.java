package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wujianlong on 2016/12/8.
 */
@Data
public class JDBatchStockSync {
    private String batchno;
    private Integer goodsStatus;
    private BigDecimal storeStock;
    private BigDecimal currentStock;
    private String storeid;
    private BigDecimal goodsid;
    private Date syncTime;


}
