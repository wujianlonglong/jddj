package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown =true)
public class JDOrderQueryOrderExtend {

    /// <summary>
    /// 订单主表订单id
    /// </summary>
    private BigDecimal orderId ;

    /// <summary>
    /// 买家省名称
    /// </summary>
    private String buyerProvinceName ;

    /// <summary>
    /// 买家市名称
    /// </summary>
    private String buyerCityName ;

    /// <summary>
    /// 买家县名称
    /// </summary>
    private String buyerCountryName;

    /// <summary>
    /// 买家镇名称
    /// </summary>
    private String buyerTownName ;

    /// <summary>
    /// 买家ip
    /// </summary>
    private String buyerIp ;

    /// <summary>
    /// 收货人地址坐标类型
    /// </summary>
    private int buyerCoordType ;

    /// <summary>
    /// 收货人地址坐标经度
    /// </summary>
    private BigDecimal buyerLat ;

    /// <summary>
    /// 收货人地址坐标纬度
    /// </summary>
    private BigDecimal buyerLng ;

    /// <summary>
    /// 发票类型
    /// </summary>
    private String orderInvoiceType ;

    /// <summary>
    /// 发票抬头
    /// </summary>
    private String orderInvoiceTitle;

    /// <summary>
    /// 发票内容
    /// </summary>
    private String orderInvoiceContent ;

    /// <summary>
    /// 订单买家备注
    /// </summary>
    private String orderBuyerRemark ;

    /// <summary>
    /// 订单商家备注
    /// </summary>
    private String orderVenderRemark ;

    /// <summary>
    /// 订单配送备注
    /// </summary>
    private String orderDeliveryRemark ;

    /// <summary>
    /// 订单客服备注
    /// </summary>
    private String orderCustomerServiceRemark ;

    /// <summary>
    /// 特殊服务标签(加热、切碎、要餐具、微辣、超辣。。。)
    /// </summary>
    private String specialServiceTag ;


    /// <summary>
    /// 购物车id
    /// </summary>
    private String cartId ;

    /// <summary>
    /// 设备id
    /// </summary>
    private String equipmentId ;

    /// <summary>
    /// 行业标签id
    /// </summary>
    private String businessTagId ;


    /// <summary>
    /// 业务标识，用英文分号分隔（订单打标写入此字段，如one.dingshida）
    /// </summary>
    private String businessTag ;

    /// <summary>
    /// 用户poi
    /// </summary>
    private String buyerPoi ;

    private String ordererName ;

    private String ordererMobile;

    private String appVersion;


    private String freightId;

    private Integer businessType;

    private Integer tips;

    public JDOrderQueryOrderExtend()
    {
        orderId =BigDecimal.ZERO;
    }
}
