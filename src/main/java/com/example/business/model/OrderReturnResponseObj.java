package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/2/22.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderReturnResponseObj extends SysResponseObj {
    private DtOrderReturnResponse data;

    public OrderReturnResponseObj() {
        data = new DtOrderReturnResponse();
    }
}