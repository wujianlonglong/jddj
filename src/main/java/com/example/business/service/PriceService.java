package com.example.business.service;

import com.example.business.BaseStockBusiness;
import com.example.business.JingdongUtil;
import com.example.business.model.*;
import com.example.business.opt.PriceOpt;
import com.example.domain.sjhub.PlatformProduct;
import com.example.domain.sjhub.PlatformShop;
import com.example.repository.sjhub.PlatformProductRepository;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.utils.property.PlatformProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wujianlong on 2017/6/5.
 */
@Service
public class PriceService extends BaseStockBusiness {

    private static final Logger log = LoggerFactory.getLogger(PriceService.class);

    @Autowired
    PlatformShopRepository platformShopRepository;

    @Autowired
    PlatformProductRepository platformProductRepository;


    @Autowired
    JingdongUtil jingdongUtil;


    @Autowired
    PlatformProperty platformProperty;



    @Autowired
    PriceOpt priceOpt;


    public ApiResult changePrice(String storeId, String goodsId, BigDecimal price) {
        ApiResult result = new ApiResult();
        try {
            PlatformShop platformShop = platformShopRepository.findBySjShopCodeAndPlatformIdAndStatus(storeId, "10002", 1);
            if (platformShop == null) {
                log.error("门店" + storeId + "在中台门店映射表中不存在可用的京东商场!");
                result.setMsg("门店" + storeId + "在中台门店映射表中不存在可用的京东商场!");
                return result;
            }
            PlatformProduct platformProduct = platformProductRepository.getUndeletedProduct("10002", goodsId);
            if (platformProduct == null) {
                log.error("商品" + goodsId + "在中台商品映射表中不存在未删除的京东商品!");
                result.setMsg("商品" + goodsId + "在中台商品映射表中不存在未删除的京东商品!");
                return result;
            }
            BigDecimal market = price;
            List<MarktPrice> marktPrices = getGoodsMarktPrice(storeId, goodsId);
            if (CollectionUtils.isNotEmpty(marktPrices)) {
                MarktPrice marktPrice = new MarktPrice();
                marktPrice = marktPrices.stream().findFirst().orElse(null);
                if (marktPrice.getMarktPrice().compareTo(price) >= 0) {
                    market = marktPrice.getMarktPrice();
                }
            }
            String jdShop = platformShop.getPlatformShopCode();
            String jdGood = platformProduct.getPlatformGoodsCode();

            JDChangePriceResponseObj jdResult = jingdongUtil.callJingdongAPIToChangePrice(jdShop, jdGood, price, market);

            if (jdResult.getSuccess()) {
                if (jdResult.getData().getCode().equals("0")) {
                    //京东端 如果刷新成功 更新数据库
                    UpdateGoodsPrice(storeId, goodsId, price, market, new Date());
                    result.setSuccess(true);
                    result.setCode("0");
                } else if (jdResult.getData().getCode().equals("181006")) {
                    log.error("修改价格失败：" + jdResult.getData().getMsg());
                    result.setMsg("修改价格失败：" + jdResult.getData().getMsg());
                    return result;
                } else {
                    log.error(jdResult.getData().getMsg());
                    result.setMsg(jdResult.getData().getMsg());
                    return result;
                }
            } else {
                log.error("调用京东改价接口失败：" + jdResult.getMsg());
                result.setMsg("调用京东改价接口失败：" + jdResult.getMsg());
                return result;
            }

        } catch (Exception ex) {
            log.error("中台修改价格失败：" + ex.toString());
            result.setMsg("中台修改价格失败：" + ex.toString());
            return result;
        }
        return result;
    }


    public ApiResult importPrice(List<PriceSync> priceSyncList) {
        ApiResult result = new ApiResult();
        try {
            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);
            //获取京东到家门店list
            List<PlatformShop> platformShopList = platformShopRepository.findByPlatformIdAndStatus(platformProperty.getJddj(), 1);
            Map<String, String> shopMap = new HashMap<>();
            platformShopList.forEach(shop -> shopMap.put(shop.getSjShopCode(), shop.getPlatformShopCode()));

            Map<String, List<PriceSync>> priceSyncMap = priceSyncList.stream().collect(Collectors.groupingBy(PriceSync::getSjShopCode));

            List<JDGoodsPriceItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchPriceSync> updateList = new ArrayList<>();//更新本地价格同步数据库List
            priceSyncMap.forEach((shop, prices) -> {
                if (!shopMap.containsKey(shop)) {
                    log.error("商场：" + shop + "不是京东到家商场！");
                    return;
                }
                //获取商品市场价
                List<MarktPrice> marktPrices = getGoodsMarktPriceList(shop);
                Map<String, BigDecimal> marktMap = new HashMap<>();
                marktPrices.forEach(markt -> marktMap.put(markt.getGoodCode(), markt.getMarktPrice()));

                String platShopCode = shopMap.get(shop);
                prices.forEach(goodsPrice -> {
                    String sjGoodsCode = goodsPrice.getSjGoodsCode();//三江商品编号
                    String platGoodsCode = platProdMap.get(sjGoodsCode);//京东商品编号
                    try {
                        if (StringUtils.isEmpty(platGoodsCode)) {
                            log.error("商品：" + sjGoodsCode + ",在商品映射表中不存在！");
                            return;
                        }
                        BigDecimal price = new BigDecimal(goodsPrice.getRetailPrice()).divide(new BigDecimal(100));
                        BigDecimal markt = price;
                        if (marktMap.containsKey(sjGoodsCode)) {
                            if (marktMap.get(sjGoodsCode).compareTo(price) > 0) {
                                markt = marktMap.get(sjGoodsCode);
                            }
                        }
                        if (price.compareTo(BigDecimal.ZERO) <= 0 || markt.compareTo(BigDecimal.ZERO) <= 0) {
                            log.error("门店：" + shop + ",商品：" + sjGoodsCode + "市场价或门店价小于等于0,不能同步价格！");
                            return;
                        }

                        JDGoodsPriceItemObj item = new JDGoodsPriceItemObj();
                        item.setStationNo(platShopCode);
                        item.setSkuId(platGoodsCode);
                        item.setPrice(price.multiply(new BigDecimal(100)).longValue());
                        item.setMarketPrice(markt.multiply(new BigDecimal(100)).longValue());
                        goodsItemList.add(item);

                        JDBatchPriceSync update = new JDBatchPriceSync();
                        update.setGoodsid(new BigDecimal(sjGoodsCode));
                        update.setStoreid(shop);
                        update.setBatchmemberprice(BigDecimal.ZERO);
                        update.setBatchmarketprice(markt);
                        update.setBatchsaleprice(price);
                        updateList.add(update);

                    } catch (Exception ex) {
                        log.error("门店：" + shop + ",商品：" + sjGoodsCode + "导入价格失败：" + ex.toString());
                        return;
                    }
                });
            });
            priceOpt.batchUpdatePrice(goodsItemList, updateList);//批量更新价格
            result.setSuccess(true);
            result.setCode("0");
        } catch (Exception ex) {
            log.error("导入价格失败：" + ex.toString());
            result.setMsg("导入价格失败：" + ex.toString());
            return result;
        }
        return result;
    }

}
