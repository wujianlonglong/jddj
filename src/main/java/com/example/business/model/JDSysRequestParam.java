package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2016/10/27.
 */
@Data
public class JDSysRequestParam {

    /**
    *采用OAuth授权方式为必填参数，必须
    */
    private String token;

    /**
     * 应用的app_key，必须
     */
    private String app_key ;

    private String app_secret ;

    /**
     * 签名，必须
     */
    private String sign ;


    /**
     * 时间戳，格式为yyyy-MM-dd HH:mm:ss
     */
    private String timestamp ;

    /**
     * 暂时只支持json，不是必须
     */
    private String format ;

    /**
     * API协议版本，可选值:1.0，不是必须
     */
    private String v ;

    /**
     *
     */
    public JDSysRequestParam()
    {
        token = "0b70ccf3-da28-4a5b-b51e-f25682e1c859";
        app_key = "77ddf5103bb64dacaac776466bd4714e";
        app_secret = "60c70f6025464568be5f32be3aec50e4";
        format = "json";
        v = "1.0";
    }



}
