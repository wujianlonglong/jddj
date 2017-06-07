package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/2/3.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderInfoQueryResponseOassBussinessSku {

    /// <summary>
    /// 订单号
    /// </summary>
    private long orderId ;

    /// <summary>
    /// 京东内部SKU的ID
    /// </summary>
    private long skuId ;

    /// <summary>
    /// 购买数量
    /// </summary>
    private int skuCount ;

    /// <summary>
    /// 销售价(优惠后的单价)
    /// </summary>
    private int promotionPrice ;

    /// <summary>
    /// 到家价(优惠类型为非单品促销时，此价格为0)
    /// </summary>
    private int pdjPrice ;

    /// <summary>
    /// 成本价(优惠类型为非单品促销时，此价格为0)
    /// </summary>
    private int costPrice ;

    /// <summary>
    /// 平台承担比例
    /// </summary>
    private int costRadio ;

    /// <summary>
    /// 商家承担比例
    /// </summary>
    private int saleRadio ;

    /// <summary>
    /// 京豆金额
    /// </summary>
    private long skuJdMoney ;

    /// <summary>
    /// 京豆商家承担比例
    /// </summary>
    private int jdSaleRedio ;

    private List<JDOrderInfoQueryResponseOrderBussiDiscountMoney> discountlist ;

    public JDOrderInfoQueryResponseOassBussinessSku()
    {
        orderId = 0;

        discountlist = new ArrayList<>();
    }
    

}
