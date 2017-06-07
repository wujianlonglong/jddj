package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/10/28.
 */
@Data
public class DtJDBatchSyncPriceResponse {

        private String code ;

        private String msg;

        private JDBatchNoResponseObj data ;

        private boolean success;

        public DtJDBatchSyncPriceResponse()
        {
            data = new JDBatchNoResponseObj();
        }

}
