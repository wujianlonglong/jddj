package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/10/28.
 */
@Data
public class JDBatchSyncPriceResponseObj extends JDSysResponseObj {
    private DtJDBatchSyncPriceResponse data ;

    public JDBatchSyncPriceResponseObj()
    {
        data = new DtJDBatchSyncPriceResponse();
    }
}
