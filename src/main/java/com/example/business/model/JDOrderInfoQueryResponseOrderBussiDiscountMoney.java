package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/2/3.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderInfoQueryResponseOrderBussiDiscountMoney {

    /// <summary>
    /// 优惠类型(1:优惠码;3:优惠劵;4:满减;5:满折;6:首单优惠)
    /// </summary>
    private int promotionType ;

    /// <summary>
    /// 优惠二级类型
    /// </summary>
    private int promotionDetailType ;

    /// <summary>
    /// 优惠金额
    /// </summary>
    private long skuDiscountMoney ;

    /// <summary>
    /// 商家承担比例
    /// </summary>
    private int saleRadio ;

    /// <summary>
    /// 促销号
    /// </summary>
    private String promotionCode ;

    public JDOrderInfoQueryResponseOrderBussiDiscountMoney()
    {

    }
}
