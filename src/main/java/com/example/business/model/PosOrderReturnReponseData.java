package com.example.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/2/20.
 */

@Data
public class PosOrderReturnReponseData {
    /// <summary>
    /// 订单号
    /// </summary>
    private String OrderId ;

    /// <summary>
    /// 订单状态
    /// </summary>
    private int OrderStatus ;

    /// <summary>
    /// 门店编号
    /// </summary>
    private String StoreId ;

    /// <summary>
    /// 订单金额，开票金额
    /// </summary>
    private BigDecimal OrderAmount ;

    /// <summary>
    /// 商品金额
    /// </summary>
    private BigDecimal GoodsAmount ;

    /// <summary>
    /// 请求Pos信息
    /// </summary>
    private PosParam Pos ;

    /// <summary>
    /// 商品列表
    /// </summary>
    private List<PosOrderReturnReponseGoods> GoodsList;

    public PosOrderReturnReponseData()
    {
        OrderId = "";
        GoodsList = new ArrayList<>();
        Pos = new PosParam();
    }
}


