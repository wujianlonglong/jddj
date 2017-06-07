package com.example.repository.sjhub;


import com.example.domain.sjhub.PriceImport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by 如水放逐 on 2016/8/31.
 */
public interface PriceImportRepository extends JpaRepository<PriceImport, String> {
    /**
     * 根据门店编码取得中台维护的商品价格数据
     *
     * @param sjShopCode 门店编码
     * @return 中台维护的商品价格数据列表
     */
    List<PriceImport> findBySjShopCode(String sjShopCode);
}
