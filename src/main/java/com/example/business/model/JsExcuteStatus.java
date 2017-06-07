package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class JsExcuteStatus {
    /// <summary>
    /// 执行结果
    /// 0：成功 >=1：失败
    /// 100-199 订单查询相关错误
    ///
    /// 200-299 订单过机相关错误
    ///         200：十分钟内已经有订单过机成功，请稍候再试或联系客服
    ///         201：京东众包增加承运商信息失败，请检查订单是否取消，如果没有取消，请联系管理人员
    ///         202：已经有过机流水（应用过机请求并发）
    ///         203：取消订单不能获取拆分明细
    /// 300-399 订单退货相关错误
    ///
    ///
    /// 9998：函数异常
    /// 9999：接口异常
    /// </summary>
    private int Status ;

    public JsExcuteStatus()
    {
        Status = 1;//默认失败
    }

}
