package com.example.business.model;

import lombok.Data;


/**
 * Created by wujianlong on 2016/10/27.
 */
@Data
public class JDBatchSyncPriceRequestObj extends JDSysRequestParam
        {
            private DtJDBatchSyncPriceRequest jd_param_json ;

            public JDBatchSyncPriceRequestObj(){
                this.jd_param_json = new DtJDBatchSyncPriceRequest();
            }

        }

