package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class DtPosReponseGoods {

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
    /// 过机需要分摊的ERP促销优惠金额
    /// </summary>
    private BigDecimal ERPDiscount;

    public DtPosReponseGoods()
    {
        BarcodeList = new ArrayList<String>();
        PromotionId = "";
        Discount=BigDecimal.ZERO;
        ERPDiscount = BigDecimal.ZERO;
    }
}
