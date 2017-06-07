package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/2/8.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderJDZBDeliveryResponseObj extends  SysResponseObj {
    private DtOrderJDZBDeliveryResponse data ;

    public OrderJDZBDeliveryResponseObj()
    {
        data = new DtOrderJDZBDeliveryResponse();
    }
}
