package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/12/7.
 */
@Data
public class DtJDBatchSyncStockResponse {
    private String code ;

    private String msg ;

    private JDBatchNoResponseObj data ;

    private boolean success;

    public DtJDBatchSyncStockResponse()
    {
        data = new JDBatchNoResponseObj();
    }

}
