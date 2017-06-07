package com.example.utils.enums;

/**
 * Created by 如水放逐 on 2016/8/15.
 */
public enum StockSyncTypeEnum {
    /**
     * 普通库存同步
     */
    ALL,
    
    /**
     * 普通库存同步
     */
    INCREMENTAL,
    /**
     * 初始化虚拟库存
     */
    INIT_VIRTUAL_STOCK,
    
    /**
     * 释放锁定库存
     */
    RELEASE_LOCK_STOCK;
}
