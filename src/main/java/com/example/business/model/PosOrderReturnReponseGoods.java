package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/2/20.
 */
@Data
public class PosOrderReturnReponseGoods
{
    /// <summary>
    /// 商品Id（管理编号）
    /// </summary>
    private String GoodsId ;

    /// <summary>
    /// 商品名称
    /// </summary>
    private String GoodsName ;

    /// <summary>
    /// 商品销售价格（分摊后价格）
    /// </summary>
    private BigDecimal SalePrice ;

    /// <summary>
    /// 商品数量
    /// </summary>
    private int GoodsCount ;

    /// <summary>
    /// 条形码
    /// </summary>
    private List<String> BarcodeList ;

    /// <summary>
    /// 参与促销Id
    /// </summary>
    private String PromotionId ;

    /// <summary>
    /// 优惠金额
    /// </summary>
    private BigDecimal Discount ;

    /// <summary>
    /// ERP中分摊的优惠
    /// </summary>
    private BigDecimal ERPDiscount ;

    public PosOrderReturnReponseGoods()
    {
        BarcodeList = new ArrayList<>();
        PromotionId = "";
        Discount = BigDecimal.ZERO;
        ERPDiscount = BigDecimal.ZERO;
    }
}

