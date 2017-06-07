package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/10.
 */
@Data
public class ProduceInfo {
    //平台编号：京东到家10002 ，三江网购10004
    private long platformId ;

    private String sjShopCode ;

    private String sjGoodsCode ;

    //减少或新增的库存数
    private Integer amount ;

    //过机：pos，退货：return,未过机取消订单：cancel，预占：prehold
    private String type ;
}
