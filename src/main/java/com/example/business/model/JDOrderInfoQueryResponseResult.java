package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/26.
 */
@Data
public class JDOrderInfoQueryResponseResult {
    /// <summary>
    /// 订单结果
    /// </summary>
    private JDOrderInfoQueryResponseOrderMain orderMain ;

    private List<JDOrderInfoQueryResponseOassBussinessSku> oassBussinessSkus;

    public JDOrderInfoQueryResponseResult()
    {
        orderMain = new JDOrderInfoQueryResponseOrderMain();
        oassBussinessSkus = new ArrayList<>();
    }
}
