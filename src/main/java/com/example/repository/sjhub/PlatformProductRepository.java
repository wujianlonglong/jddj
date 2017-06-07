package com.example.repository.sjhub;


import com.example.domain.sjhub.PlatformProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by gaoqichao on 16-7-15.
 */
public interface PlatformProductRepository extends JpaRepository<PlatformProduct, Long> {
    /**
     * 取得指定状态和平台的商品关系列表
     *
     * @param platformId 平台id
     * @return 平台商品关系列表
     */
    List<PlatformProduct> findByPlatformId(String platformId);

    /**
     * 取得指定平台和商品id的商品关系列表
     *
     * @param platformId  平台id
     * @param sjGoodsCode 商品编码
     * @return 商品关系列表
     */
    List<PlatformProduct> findByPlatformIdAndSjGoodsCode(String platformId, String sjGoodsCode);


    /**
     * 根据平台号和状态取得商品列表
     *
     * @param platformId 平台id
     * @param status 状态
     * @return
     */
    List<PlatformProduct> findByPlatformIdAndStatus(String platformId,int status);

    /**
     * 根据平台号和商品编码取得未删除的记录
     *
     * @param platformId  平台id
     * @param sjGoodsCode 商品编码
     * @return
     */
    @Query(value = "select * from PLATFORM_PRODUCT where PLATFORM_ID = :platformId and SJ_GOODS_CODE = :sjGoodsCode " +
            " and status in (1,2)", nativeQuery = true)
    PlatformProduct getUndeletedProduct(@Param("platformId") String platformId,
                                        @Param("sjGoodsCode") String sjGoodsCode);
}
