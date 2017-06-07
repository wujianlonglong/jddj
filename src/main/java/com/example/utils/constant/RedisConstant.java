package com.example.utils.constant;

/**
 * Created by gaoqichao on 16-7-21.
 * redis库存常量
 */
public interface RedisConstant {
    /**
     * 库存同步
     */
    String STOCK_SYNC_PREFIX = "stockSync:shop:";

    /**
     * 京东库存同步
     */
    String STOCK_SYNCJDDJ_PREFIX = "stockSyncjddj:shop:";
    
    /**
     * 虚拟库存前缀
     */
    String VIRTUAL_STOCK_PREFIX = "virtualStock:shop:";
    
    /**
     * 虚拟库存前缀
     */
    String VIRTUAL_STOCK_PLAN_PREFIX = "virtualStockPlan:shop:";
    
    /**
     * 单个门店虚拟库存列表
     */
    String SHOP_VIRTUAL_STOCK_LIST = "virtualStockList:shop:";
    
    /**
     * 单个门店虚拟库存列表
     */
    String SHOP_VIRTUAL_STOCK_PLAN_LIST = "virtualStockPlanList:";
    
    /**
     * 价格同步
     */
    String PRICE_SYNC_PREFIX = "priceSync:shop:";
    
    /**
     * 平台与三江的商品编码映射,key为三江商品编码
     */
    String PLAT_PROD_MAP_SJ = "platProdMap:sj:";
    
    /**
     * 平台与三江的商品编码映射,key为平台商品编码
     */
    String PLAT_PROD_MAP_PLAT = "platProdMap:plat:";

    /***
     * 平台与三江的商场编号映射，key为三江商场编号
     */
     String PLAT_SHOP_MAP_SJ="platShopMap:sj:";

    /***
     * 平台与三江的商场编号映射，key为平台商场编号
     */
    String PLAT_SHOP_MAP_PLAT="platShopMap:plat:";

    /**
     * 上次同步库存时间
     */
    String LAST_STOCK_SYNC_TIME = "lastStockSyncTime";

    /**
     * 京东上次同步库存时间
     */
    String LAST_STOCK_SYNCJDDJ_TIME = "lastStockSyncjddjTime";


    /**
     * 上次同步价格时间
     */
    String LAST_PRICE_SYNC_TIME = "lastPriceSyncTime";

    /**
     * 京东上次同步价格时间
     */
    String LAST_PRICE_SYNCJDDJ_TIME="lastPriceSyncjddjTime";

    /**
     * 有问题的库存同步流水号
     */
    String ERROE_STOCK_BATCH_NO="ErrorStockBatchNo:vir:";
}
