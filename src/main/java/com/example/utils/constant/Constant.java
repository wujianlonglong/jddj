package com.example.utils.constant;

import java.math.BigDecimal;

/**
 * Created by gaoqichao on 16-6-24.
 */
public interface Constant {
    /**
     * 响应码成功
     */
    int CODE_SUCCESS = 0;
    
    /**
     * 响应码失败
     */
    int CODE_FAIL = 1;
    
    /**
     * 响应消息成功
     */
    String MESSAGE_SUCCESS = "success";
    
    /**
     * 门店状态-启用
     */
    int SHOP_STATUS_ENABLE = 1;
    
    /**
     * 门店状态-禁用
     */
    int SHOP_STATUS_DISENABLE = 0;
    
    /**
     * 同步标志:同步
     */
    int SYNC_FLAG_ENABLE = 1;
    
    /**
     * 同步标志-不同步
     */
    int SYNC_FLAG_DISABLE = 0;
    
    /**
     * 100
     */
    BigDecimal BIG_DECIMAL_HUNDRED = new BigDecimal(100);

    /**
     * 过机请求查询
     */
    int POS_PROCESS_TYPE_QUERY = 0;

    /**
     * 过机请求过机
     */
    int POS_PROCESS_TYPE_POS = 1;

    /**
     * 过机请求取消
     */
    int POS_PROCESS_TYPE_CANCEL = 2;


    /**
     * 有效性
     */
    int VIRPLAN_STATUS_ENABLE=1;


    String JDDJ_PICK_UP_PRODUCT_FINISH = "10132";
    String JDDJ_PICK_UP_SUCCESS = "0";
    String JDDJ_PICK_UP_ABNORMAL = "10101";


}
