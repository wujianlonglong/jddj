package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderQueryOrderProduct {



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
    /// 商品的名称+SKU规格
    /// </summary>
    private String skuName ;

    /// <summary>
    /// SKU外部ID
    /// </summary>
    private String skuIdIsv ;

    /// <summary>
    /// 京东内部商品ID(一个spu下有多个sku比如尺码或颜色不同，spu相同，sku不同)
    /// </summary>
    private BigDecimal skuSpuId ;

    /// <summary>
    ///  SKU京东销售价（分）
    /// </summary>
    private int skuJdPrice;

    /// <summary>
    /// 数量
    /// </summary>
    private int skuCount ;

    /// <summary>
    /// 库存归属（生鲜、冷藏、冷冻、发码）——拆单
    /// </summary>
    private int skuStockOwner ;

    /// <summary>
    /// 0：默认值非赠品；1：赠品
    /// </summary>
    @JsonProperty("isGift")
    private Boolean isGift ;

    /// <summary>
    /// 调整方式(0：默认值，没调整，原订单明细；1:新增，2：删除，3：修改数量）
    /// </summary>
    private int adjustMode ;

    /// <summary>
    /// 加热、切碎、要餐具、微辣、超辣。。。
    /// </summary>
    private String specialServiceTag ;

    /// <summary>
    /// 商品upc码
    /// </summary>
    private String upcCode ;

    private String categoryId ;

    private String supplyShortCode ;

    private String supplyName ;

    private int skuStorePrice ;

    private Integer skuCostPrice ;

    /// <summary>
    /// 0：没有任何优惠 2：秒杀 3：单品
    /// </summary>
    private Integer promotionType ;
}
