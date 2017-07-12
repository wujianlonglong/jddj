package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/15.
 */
@Data
public class JDGoodsInfoResponseObj extends JDSysResponseObj{
    private DtJDGoodsInfoResponse data ;
    public JDGoodsInfoResponseObj()
    {
        data = new DtJDGoodsInfoResponse();
    }
}
