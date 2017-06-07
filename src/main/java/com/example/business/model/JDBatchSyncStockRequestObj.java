package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/12/7.
 */
@Data
public class JDBatchSyncStockRequestObj extends JDSysRequestParam{
    private DtJDBatchSyncStockRequest jd_param_json ;

    public JDBatchSyncStockRequestObj()
    {
        jd_param_json = new DtJDBatchSyncStockRequest();
    }
}
