package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/1/26.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public  class JDOrderInfoQueryOrderDiscount {

    /// <summary>
    /// 订单主表订单id
    /// </summary>
    private long orderId ;

    /// <summary>
    /// 京东内部SKU的ID
    /// </summary>
    private String skuId ;

    /// <summary>
    /// 记录参加活动的sku数组
    /// </summary>
    private String skuIds ;

    /// <summary>
    /// 优惠类型优惠类型
    /// </summary>
    private int discountType ;

    /// <summary>
    /// 小优惠类型
    /// </summary>
    private int discountDetailType ;

    /// <summary>
    /// 优惠券编号
    /// </summary>
    private String discountCode ;

    /// <summary>
    /// 优惠金额
    /// </summary>
    private int discountPrice ;

    public JDOrderInfoQueryOrderDiscount()
    {
        skuIds = "";
    }
}
