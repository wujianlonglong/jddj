package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/2/10.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDAfsResponseAfsDetail {

    /// <summary>
    /// 京东到家商品编码
    /// 
    /// </summary>
    private long wareId ;

    /// <summary>
    /// 商品名称
    /// 
    /// </summary>
    private String wareName ;

    /// <summary>
    /// 商家商品编码
    /// </summary>
    private String skuIdIsv ;

    private long payPrice ;

    private int skuCount ;

    /// <summary>
    /// 该sku该售后单原始总金额（skuMoney=payPrice*skuCount）
    /// </summary>
    private long skuMoney ;

    /// <summary>
    /// 该sku此次售后应退款金额（afsMoney=skuMoney-virtualMoney）
    /// </summary>
    private long afsMoney ;

    /// <summary>
    /// 该sku此次售后实际退用户实际支付金额（cashMoney=afsMoney-jdBeansMoney）
    /// </summary>
    private long cashMoney ;

    /// <summary>
    /// 该sku此次售后实际退用户京豆支付金额
    /// </summary>
    private long jdBeansMoney ;

    /// <summary>
    /// 该SKU实际售后个数总优惠金额，等于该sku维度满减优惠金额+首单优惠金额+优惠券/码优惠金额总和
    /// </summary>
    private long virtualMoney ;

    private List<JDAfsResponseAfsDiscount> discountLst ;

    public JDAfsResponseAfsDetail()
    {
        discountLst=new ArrayList<>();
    }
}
