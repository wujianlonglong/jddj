package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/26.
 */
@Data
public class JDOrderInfoQueryResponseObj extends JDSysResponseObj
{

    private JDOrderInfoQueryResponseData data ;

    public JDOrderInfoQueryResponseObj()
    {
        data = new JDOrderInfoQueryResponseData();
    }

}
