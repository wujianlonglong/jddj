package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/22.
 */
@Data
public class ERPPromotionObj {
    private String PromotionId ;

    private String JDDJPromotionId ;

    /// <summary>
    /// 1：满减
    /// 2：
    /// </summary>
    private int PromotionType ;

    private Date StartDate;

    private Date EndDate ;

    private List<String> GoodsId ;

    private List<String> StoreId ;

    public ERPPromotionObj()
    {
        GoodsId = new ArrayList<>();
        StoreId = new ArrayList<>();
    }
}
