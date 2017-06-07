package com.example.repository.sjhub;


import com.example.domain.sjhub.PlatformShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by gaoqichao on 16-7-6.
 */
public interface PlatformShopRepository extends JpaRepository<PlatformShop, Long> {
    
    /**
     * 取得所有启用状态的门店门店信息
     *
     * @param status 状态
     * @return
     */
    List<PlatformShop> findByStatus(int status);
    
    /**
     * 根据平台门店编码取得门店信息
     *
     * @param platformShopCode 平台门店编码
     * @return 门店信息
     */
    PlatformShop findByPlatformShopCodeAndPlatformId(String platformShopCode, String platformId);
    
    /**
     * 取得对应状态的门店平台对应关系
     *
     * @param status 状态
     * @return
     */
    List<PlatformShop> findByPlatformIdAndStatus(String platformId, int status);
    
    /**
     * 根据平台门店编码取得门店信息
     *
     * @param sjShopCode 三江门店编码
     * @param platformId 平台id
     * @return 门店信息
     */
    PlatformShop findBySjShopCodeAndPlatformId(String sjShopCode, String platformId);


    /**
     * 根据平台门店编码取得门店信息
     *
     * @param sjShopCode 三江门店编码
     * @param platformId 平台id
    * @param status 状态
     * @return 门店信息
     */
    PlatformShop findBySjShopCodeAndPlatformIdAndStatus(String sjShopCode, String platformId, int status);


    /**
     * 根据平台门店编码取得门店信息
     *
     * @param platformId 平台id
     * @param platformShopCode 平台门店编号
     * @param status 状态
     * @return 门店信息
     */
    PlatformShop findByPlatformIdAndPlatformShopCodeAndStatus(String platformId, String platformShopCode, int status);

}
