package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/10/28.
 */
@Data
public class JDSysResponseObj {
    private String code ;

    private String msg ;

    private Boolean success ;

    public JDSysResponseObj()
    {
        code = "";
        msg = "";
        success = false;
    }
}
