package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class OrderInfoRequestPara extends SysRequestPara{
    private DtOrderInfoRequest jd_param_json ;
    public OrderInfoRequestPara()
    {
        jd_param_json = new DtOrderInfoRequest();
    }


}
