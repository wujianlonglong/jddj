package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/12/7.
 */
@Data
public class JDBatchSyncStockResponseObj  extends JDSysResponseObj {
    private DtJDBatchSyncStockResponse data ;

    public JDBatchSyncStockResponseObj()
    {
        data = new DtJDBatchSyncStockResponse();
    }
}
