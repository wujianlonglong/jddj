package com.example.repository.sjhub;

import com.example.domain.sjhub.StockVirtualPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wujianlong on 2017/3/30.
 */
public interface StockVirtualPlanRepository extends JpaRepository<StockVirtualPlan,String> {
    /**
     * 按时间取得所有设置了初始化库存的商品计划
     *
     * @param virSyncTime 初始化时间
     * @param virStatus   初始化状态
     * @return 初始化库存的商品计划
     */
    List<StockVirtualPlan> findByVirSyncTimeAndVirStatus(String virSyncTime, int virStatus);

    /**
     * 按时间取得所有设置了释放锁定库存的商品计划
     *
     * @param virLockSyncTime 释放锁定库存时间
     * @param virLockStatus   释放状态
     * @return 释放锁定库存的商品计划
     */
    List<StockVirtualPlan> findByVirLockSyncTimeAndVirLockStatus(String virLockSyncTime, int virLockStatus);

    /**
     * 根据门店和商品编码取得虚拟库存计划信息
     *
     * @param sjShopCode  三江门店编码
     * @param sjGoodsCode 商品编码
     * @return 虚拟库存计划信息
     */
    StockVirtualPlan findBySjShopCodeAndSjGoodsCodeAndVirStatus(String sjShopCode, String sjGoodsCode, int virStatus);

    /**
     * 根据虚拟库存同步状态取得所有的设置了虚拟库存并同步的信息
     *
     * @param sjShopCode 门店编码
     * @return 虚拟库存数据列表
     */
    List<StockVirtualPlan> findBySjShopCodeAndVirStatus(String sjShopCode, int virStatus);


    /**
     * 根据虚拟库存同步状态取得所有的设置了虚拟库存并同步的信息
     *
     * @param sjShopCode 门店编码
     * @return 虚拟库存数据列表
     */
    List<StockVirtualPlan> findBySjShopCodeAndVirStatusAndGoodsStatus(String sjShopCode, int virStatus,int goodsStatus);


    /**
     * 根据虚拟库存同步状态取得所有的设置了虚拟库存并同步的信息
     *
     * @param sjGoodsCode 商品编码
     * @return 虚拟库存数据列表
     */
    List<StockVirtualPlan> findBySjGoodsCodeAndVirStatus(String sjGoodsCode, int virStatus);


    /**
     * 根据虚拟库存同步状态和有效性获得所有的设置了虚拟库存并同步的信息
     *
     * @param sjGoodsCode 商品编号
     * @param virStatus 同步状态
     * @patam goodsStatus 有效性
     */
    List<StockVirtualPlan> findBySjGoodsCodeAndVirStatusAndGoodsStatus(String sjGoodsCode,int virStatus,int goodsStatus);


    /**
     * 根据虚拟库存同步状态和有效性获得虚拟库存信息
     *
     * @param virStatus 同步状态
     * @param goodsStatus 有效性
     * @return
     */
    List<StockVirtualPlan> findByVirStatusAndGoodsStatus(int virStatus,int goodsStatus);

}
