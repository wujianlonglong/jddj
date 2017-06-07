package com.example.repository.sjhub;

import com.example.domain.sjhub.StockVirtualPlan;
import com.example.domain.sjhub.StockVirtualSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by wujianlong on 2017/3/30.
 */
public interface StockVirtualSyncRepository extends JpaRepository<StockVirtualSync,String> {

    @Query(nativeQuery=true,value ="select a.* from STOCK_VIRTUAL_SYNC a,STOCK_VIRTUAL_PLAN b where a.sj_shop_code=b.sj_shop_code and a.sj_goods_code=b.sj_goods_code and b.vir_status=1 and goods_status=1")
    public List<StockVirtualSync> getValidVirStock();

}
