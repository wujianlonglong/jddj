package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/22.
 */
@Data
public class DtOrderReturnRequest {

    private String orderId ;

    private String operateTime ;

    public DtOrderReturnRequest()
    {
        orderId = "";
        operateTime = "";
    }
}
