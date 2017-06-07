package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/22.
 */
@Data
public class TempERPPromotion
{
    /// <summary>
    /// 京东到家商家中心 促销活动Id
    /// </summary>
    private String JDDJPromotionId ;

    /// <summary>
    /// ERP促销活动Id
    /// </summary>
    private String ERPPromotionId ;

    /// <summary>
    /// 优惠金额（分）
    /// </summary>
    private int Discount ;

    private List<String> SkuIdList ;

    /// <summary>
    /// 商品总金额
    /// </summary>
    private int Amount ;

    public TempERPPromotion()
    {
        SkuIdList = new ArrayList<>();
    }
}

