package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/15.
 */
@Data
public class JDGoodsInfoRequestObj extends  JDSysRequestParam{
    public DtJDGoodsInfoRequest jd_param_json ;

    public JDGoodsInfoRequestObj()
    {
        jd_param_json = new DtJDGoodsInfoRequest();
    }
}
