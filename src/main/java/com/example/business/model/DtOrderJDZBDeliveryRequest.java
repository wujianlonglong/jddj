package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/8.
 */
@Data
public class DtOrderJDZBDeliveryRequest {
    private String orderId ;

    private String operator;

    public DtOrderJDZBDeliveryRequest()
    {
        orderId = "";
        operator = "";
    }
}
