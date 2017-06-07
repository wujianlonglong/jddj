package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/3/31.
 */
@Data
public class JDBatchModifyStockResultRequestPara  extends JDSysRequestParam {
    private DtBatchModifyStockResultRequest jd_param_json ;

    public JDBatchModifyStockResultRequestPara()
    {
        jd_param_json = new DtBatchModifyStockResultRequest();
    }
}
