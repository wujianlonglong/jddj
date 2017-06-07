package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderQueryOrderDiscount {
    /// <summary>
    /// 表记录自增id
    /// </summary>
    private BigDecimal id ;

    /// <summary>
    /// 订单主表订单id
    /// </summary>
    private BigDecimal orderId ;

    /// <summary>
    /// 调整单记录id（0：原单商品明细,非0：调整单id 或者 确认单id)
    /// </summary>
    private BigDecimal adjustId ;

    /// <summary>
    /// 京东内部SKU的ID
    /// </summary>
    private int skuId ;

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

    public JDOrderQueryOrderDiscount()
    {
        skuIds = "";
    }



}
