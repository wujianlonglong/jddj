package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/22.
 */
@Data
public class JDAfsConfirmReceiptRequestParam
{
    /// <summary>
    /// 服务单号
    /// </summary>
    private String afsServiceOrder ;

    /// <summary>
    /// 操作人
    /// </summary>
    private String pin ;

    /// <summary>
    /// 门店编号（京东到家门店编号）
    /// </summary>
    private String stationNo ;

}
