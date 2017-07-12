package com.example.business.opt;

import com.example.business.JingdongUtil;
import com.example.business.model.DtJDGoodsInfoResponseGoods;
import com.example.domain.sjhub.PlatformProduct;
import com.example.repository.sjhub.PlatformProductRepository;
import com.example.utils.property.PlatformProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wujianlong on 2017/6/15.
 */
@Service
public class ProductOpt {

    private static final Logger log = LoggerFactory.getLogger(ProductOpt.class);

    @Autowired
    PlatformProperty platformProperty;

    @Autowired
    JingdongUtil jingdongUtil;

    @Autowired
    protected PlatformProductRepository platProductRepository;

   // @Scheduled(cron = "0 0 7,11,15,19 * * *")
    public void productSync() {
        log.info("开始全量同步京东到家商品至中台商品！");
        int productCount = jingdongUtil.getJdProductCount();
        if (productCount == 0) {
            log.error("获取京东到家商品数量失败！");
            return;
        }

        List<PlatformProduct> platProdList = platProductRepository.findByPlatformId(platformProperty.getJddj());
        Map<String, PlatformProduct> productMap = new HashMap<>();
        platProdList.forEach(p -> productMap.put(p.getSjGoodsCode(), p));

        int page = 1;
        int size = 50;
        int total = (((new BigDecimal(productCount)).divide(new BigDecimal(size))).setScale(0, RoundingMode.UP)).intValue();
        List<PlatformProduct> insertList = new ArrayList<>();//需要新增的商品
        List<PlatformProduct> updateList = new ArrayList<>();//需要更新的商品
        while (true) {
            getUpProducts(insertList, updateList, productMap, page, size);
            if (page >= total) {
                break;
            }
            page++;
        }
        //更新中台商品映射表
        platProductRepository.save(updateList);
        //中台商品映射表新增商品
        insertProduct(insertList);

    }

    // TODO: 2017/6/15  需要写到另一个类中，要不然事务注解无效
    @Transactional
    public void insertProduct(List<PlatformProduct> insertList) {
        platProductRepository.save(insertList);
// TODO: 2017/6/15  新增对应两个stock数据
    }

    /**
     * 获取需要更新或新增的商品
     *
     * @param productMap
     * @param page
     * @param size
     */
    public void getUpProducts(List<PlatformProduct> insertList, List<PlatformProduct> updateList, Map<String, PlatformProduct> productMap, int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        List<DtJDGoodsInfoResponseGoods> jdProductList = jingdongUtil.getJdProductList(page, size);
        jdProductList.forEach(jdProduct -> {
            String sjGoodCode = jdProduct.getOutSkuId();
            PlatformProduct centerProduct = productMap.get(sjGoodCode);
            String jdGoodCode = jdProduct.getSkuId();//京东sku
            Integer jdStatus = jdProduct.getFixedStatus();//京东商品上下架状态(1:上架;2:下架;4:删除;)
            if (null == centerProduct) {
                //新增
                PlatformProduct product = new PlatformProduct();
                product.setPlatformId("10002");
                product.setSjGoodsCode(sjGoodCode);
                product.setPlatformGoodsCode(jdGoodCode);
                product.setStatus(jdStatus);
                product.setCreateTime(now);
                product.setCreateBy(new Long(10002));
                product.setUpdateTime(now);
                insertList.add(product);

            } else {
                //更新
                String centerGoodCode = centerProduct.getPlatformGoodsCode();//中台的京东ku
                Integer centerStatus = centerProduct.getStatus();//中台商品状态
                //如果没有变化就不更新
                if (jdGoodCode.equals(centerGoodCode) && jdStatus.equals(centerStatus)) {
                    return;
                }
                centerProduct.setPlatformGoodsCode(jdGoodCode);
                centerProduct.setStatus(jdStatus);
                centerProduct.setUpdateTime(now);
                updateList.add(centerProduct);
            }

        });
    }

}
