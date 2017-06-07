package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/8.
 */
@Data
public class OrderJDZBDeliveryRequestPara extends SysRequestPara {
    private DtOrderJDZBDeliveryRequest jd_param_json;

    public OrderJDZBDeliveryRequestPara() {
        jd_param_json = new DtOrderJDZBDeliveryRequest();
    }
}
