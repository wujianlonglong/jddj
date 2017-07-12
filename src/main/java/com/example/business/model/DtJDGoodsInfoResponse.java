package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/15.
 */
@Data
public class DtJDGoodsInfoResponse {
    private String code ;

    private String msg ;

    private boolean success ;

    private DtJDGoodsInfoResponseResult result ;

    public DtJDGoodsInfoResponse()
    {
        result = new DtJDGoodsInfoResponseResult();
    }
}
