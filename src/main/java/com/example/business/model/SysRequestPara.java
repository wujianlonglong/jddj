package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class SysRequestPara {

    /// <summary>
    /// 采用OAuth授权方式为必填参数，必须
    /// </summary>
    private String token ;

    /// <summary>
    /// 应用的app_key，必须
    /// </summary>
    private String app_key;

    private String app_secret ;

    /// <summary>
    /// 签名，必须
    /// </summary>;{ get; set; }

    /// <summary>
    /// 时间戳，格式为yyyy-MM-dd HH:mm:ss
    /// </summary>
    private String timestamp ;

    /// <summary>
    /// 暂时只支持json，不是必须
    /// </summary>
    private String format ;

    /// <summary>
    /// API协议版本，可选值:1.0，不是必须
    /// </summary>
    private String v ;

    public SysRequestPara()
    {
        token = "0b70ccf3-da28-4a5b-b51e-f25682e1c859";
        app_key = "77ddf5103bb64dacaac776466bd4714e";
        app_secret = "60c70f6025464568be5f32be3aec50e4";
        format = "json";
        v = "1.0";
    }

}
