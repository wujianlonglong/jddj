package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/12.
 */
@Data
public class JDSingleStockUpdateRequestPara extends  JDSysRequestParam {
    public DtSingleStockUpdateRequest jd_param_json ;

    public JDSingleStockUpdateRequestPara()
    {
        jd_param_json = new DtSingleStockUpdateRequest();
    }

}
