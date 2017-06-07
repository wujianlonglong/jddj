package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

/**
 * Created by wujianlong on 2017/6/5.
 */
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceSync {
    private long id;

    /**
     * 三江门店编码
     */
    private String sjShopCode;
    /**
     * 商品编码
     */
    private String sjGoodsCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 同步状态:0-不同步;1-同步
     */
    private int status;

    /**
     * 商品零售价
     */
    private long retailPrice;
}
