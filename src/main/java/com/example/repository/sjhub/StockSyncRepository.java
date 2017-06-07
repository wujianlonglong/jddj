package com.example.repository.sjhub;


import com.example.domain.sjhub.StockSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by gaoqichao on 16-7-20.
 */
public interface StockSyncRepository extends JpaRepository<StockSync, Long> {
    /**
     * 取得指定门店和商品的上一次同步信息
     *
     * @param shopCode    门店编码
     * @param sjGoodsCode 商品编码
     * @return 同步信息
     */
    StockSync findByShopCodeAndSjGoodsCode(String shopCode, String sjGoodsCode);
    
    /**
     * 取得指定门店的库存同步信息列表
     *
     * @param shopCode 门店编码
     * @return 库存同步信息列表
     */
    List<StockSync> findByShopCode(String shopCode);

    /**
     * 取得指定商品的库存同步信息列表
     *
     * @param sjGoodsCode 门店编码
     * @return 库存同步信息列表
     */
    List<StockSync> findBySjGoodsCode(String sjGoodsCode);


    /**
     * 取得指定门店可同步的库存同步信息列表
     *
     * @param shopCode 门店编号
     * @param syncFlag 同步状态
     * @return
     */
    List<StockSync> findByShopCodeAndSyncFlag(String shopCode,int syncFlag);
}
