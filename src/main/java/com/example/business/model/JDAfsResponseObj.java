package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/10.
 */
@Data
public class JDAfsResponseObj extends JDSysResponseObj {
    private JDAfsResponseData data ;

    public JDAfsResponseObj()
    {
        data = new JDAfsResponseData();
    }
}
