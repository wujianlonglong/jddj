package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class JDOrderQueryResponseObj extends JDSysResponseObj{
    private JDOrderQueryResponseData data ;

    public JDOrderQueryResponseObj()
    {
        data = new JDOrderQueryResponseData();
    }
}
