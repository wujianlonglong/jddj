package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/5.
 */
@Data
public class JDChangePriceRequestPara extends JDSysRequestParam {
    private DtChangePriceRequest jd_param_json;

    public JDChangePriceRequestPara() {
        jd_param_json = new DtChangePriceRequest();
    }
}
