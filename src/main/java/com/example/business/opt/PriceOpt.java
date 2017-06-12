package com.example.business.opt;

import com.example.business.BaseStockBusiness;
import com.example.business.CommonJingdong.Common;
import com.example.business.JingdongUtil;
import com.example.business.model.*;
import com.example.domain.sjhub.LastGoodsPrice;
import com.example.domain.sjhub.PlatformShop;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.utils.JsonUtil;
import com.example.utils.constant.Constant;
import com.example.utils.constant.RedisConstant;
import com.example.utils.property.JddjProperty;
import com.example.utils.property.PlatformProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by wujianlong on 2017/6/2.
 */
@Service
public class PriceOpt extends BaseStockBusiness {

    private static final Logger log = LoggerFactory.getLogger(PriceOpt.class);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    PlatformProperty platformProperty;

    @Autowired
    PlatformShopRepository shopRepository;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Timestamp> lastSyncPriceTiemOperations;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, String> virStockBatchOperations;

    @Autowired
    JddjProperty jddjProperty;

    @Autowired
    JingdongUtil jingdongUtil;

    @Autowired
    Common common;

    /**
     * 全量同步价格（导入的商品不同步价格）
     */
    @Scheduled(cron = "0 30 1 * * *")
    public void allPriceSync() {
        String syncTime = simpleDateFormat.format(new Date());
        log.info(syncTime + "---------------全量同步门店商品价格---开始!");

        try {
            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);
            //获取京东到家门店list
            List<PlatformShop> platformShopList = shopRepository.findByPlatformIdAndStatus(platformProperty.getJddj(), 1);
            //获取导入的商品（导入的商品不同步价格）
            Map<String, List<String>> priceImportMapGroupByShop = this.getPriceImportMapGroupByShop();

            List<JDGoodsPriceItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchPriceSync> updateList = new ArrayList<>();//更新本地价格同步数据库List

            platformShopList.forEach(obj -> {
                String sjShop = obj.getSjShopCode();
                log.info("门店：" + sjShop + "全量同步价格开始！");
                try {
                    //获得指定商场所有商品价格信息
                    List<GoodsPrice> goodsPriceList = this.getGoodsPriceListByShop(sjShop);
                    if (CollectionUtils.isEmpty(goodsPriceList)) {
                        log.error("门店:" + sjShop + "没有可同步价格的商品！");
                        return;
                    }
                    List<String> noSyncList = priceImportMapGroupByShop.get(sjShop);
                    if (CollectionUtils.isNotEmpty(noSyncList)) {
                        //筛选非导入的商品
                        goodsPriceList = goodsPriceList.stream().filter(p -> !noSyncList.contains(p.getGoodsCode())).collect(Collectors.toList());
                    }
                    if(CollectionUtils.isEmpty(goodsPriceList)){
                        log.error("门店:" + sjShop + "没有可同步价格的商品！");
                        return;
                    }
                    //获取商品市场价
                    List<MarktPrice> marktPrices= getGoodsMarktPriceList(sjShop);
                    Map<String,BigDecimal> marktMap=new HashMap<>();
                    marktPrices.forEach(markt->marktMap.put(markt.getGoodCode(),markt.getMarktPrice()));

                    String platShopCode = obj.getPlatformShopCode();//京东到家门店编号

                    goodsPriceList.forEach(goodsPrice->{
                        String sjGoodsCode = goodsPrice.getGoodsCode();//三江商品编号
                        try{
                            String platGoodsCode = platProdMap.get(sjGoodsCode);//京东商品编号
                            if (StringUtils.isEmpty(platGoodsCode)) {
                                log.error("商品：" + sjGoodsCode + ",在商品映射表中不存在！");
                                return;
                            }

                            BigDecimal price=goodsPrice.getSalePrice();
                            BigDecimal markt=price;
                            if(marktMap.containsKey(sjGoodsCode)){
                                if(marktMap.get(sjGoodsCode).compareTo(price)>0){
                                    markt=marktMap.get(sjGoodsCode);
                                }
                            }
                            if(price.compareTo(BigDecimal.ZERO)<=0||markt.compareTo(BigDecimal.ZERO)<=0){
                                log.error("门店：" + sjShop + ",商品：" + sjGoodsCode + "市场价或门店价小于等于0,不能同步价格！");
                                return;
                            }

                            JDGoodsPriceItemObj item=new JDGoodsPriceItemObj();
                            item.setStationNo(platShopCode);
                            item.setSkuId(platGoodsCode);
                            item.setPrice(price.multiply(new BigDecimal(100)).longValue());
                            item.setMarketPrice(markt.multiply(new BigDecimal(100)).longValue());
                            goodsItemList.add(item);

                            JDBatchPriceSync update=new JDBatchPriceSync();
                            update.setGoodsid(new BigDecimal(sjGoodsCode));
                            update.setStoreid(sjShop);
                            update.setBatchmemberprice(BigDecimal.ZERO);
                            update.setBatchmarketprice(markt);
                            update.setBatchsaleprice(price);
                            updateList.add(update);
                        }
                        catch(Exception ex){
                            log.error("门店：" + sjShop + ",商品：" + sjGoodsCode + "全量同步价格错误：" + ex.toString());
                            return;
                        }
                    });
                } catch (Exception ex) {
                    log.error("门店：" + sjShop + "全量同步价格错误:" + ex.toString());
                }
                log.info("门店：" + sjShop + "全量同步价格结束！");
            });
//            List<JDBatchPriceSync>  priceSyncs=getPriceSync("00008");
//            Map<BigDecimal,JDBatchPriceSync> prMap=new HashMap<>();
//            priceSyncs.forEach(p->prMap.put(p.getGoodsid(),p));
//            updateList.forEach(update->{
//                    BigDecimal goodid = update.getGoodsid();
//
//                    JDBatchPriceSync jj = prMap.get(goodid);
//                try {
//                    if (null != jj) {
//                        if (!jj.getBatchsaleprice().equals(update.getBatchsaleprice()) || !jj.getBatchmarketprice().equals(update.getBatchmarketprice())) {
//                            log.error("商品：" + goodid + "全量售价或市场价和京东价格不一样，全量售价：" + update.getBatchsaleprice() + "市场价：" + update.getBatchmarketprice() + "京东售价：" + jj.getBatchsaleprice() + "市场价：" + jj.getBatchmarketprice());
//                        }
//                    }
//                }
//                catch(Exception ex){
//
//                    log.error(update+":"+jj);
//                }
//            });
            batchUpdatePrice(goodsItemList, updateList);//批量更新库存
            //将同步价格的时间保存至redis,以便增量同步使用
            lastSyncPriceTiemOperations.set(RedisConstant.LAST_PRICE_SYNCJDDJ_TIME, Timestamp.valueOf(syncTime));

        } catch (Exception ex) {
            log.error("全量同步门店商品价格出错：" + ex.toString());
        } finally {
            String end = simpleDateFormat.format(new Date());
            log.info(end + "-----全量同步门店商品价格---结束！");
        }

    }



    /**
     * 增量同步价格（导入的商品不同步价格）
     */
    @Scheduled(cron = "0 0 7-18 * * *")
    public void incrementalPriceSync() {
        LocalDateTime now = LocalDateTime.now();
        String start = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info(start + "---------------增量同步门店价格---开始!");

        try {
            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);

            List<PlatformShop> platformShopList = shopRepository.findByPlatformIdAndStatus(platformProperty.getJddj(), Constant.SHOP_STATUS_ENABLE);
            if (CollectionUtils.isEmpty(platformShopList)) {
                log.error("中台没有京东到家的有效商场！");
                return;
            }
          //  List<String> shopList = new ArrayList<>();//用来sql查询的商场列表
            Map<String, String> shopMap = new HashMap<>(); //获取京东到家商场映射
            platformShopList.forEach(shop -> {
               // shopList.add(shop.getSjShopCode());
                shopMap.put(shop.getSjShopCode(), shop.getPlatformShopCode());
            });

            List<LastGoodsPrice> goodsPriceList = this.getIncrementalGoodsPrice(now);
            if (CollectionUtils.isEmpty(goodsPriceList)) {
                log.error("没有可增量价格同步的商品！");
                return;
            }
            Map<String, List<LastGoodsPrice>> priceGroupByShop
                    = goodsPriceList.stream()
                    .collect(Collectors.groupingBy(LastGoodsPrice::getShopCode));

            //获取导入的商品（导入的商品不同步价格）
            Map<String, List<String>> priceImportMapGroupByShop = this.getPriceImportMapGroupByShop();

            List<JDGoodsPriceItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchPriceSync> updateList = new ArrayList<>();//更新本地价格同步数据库List

            priceGroupByShop.forEach((shop,prices)->{
                List<String> importList = priceImportMapGroupByShop.get(shop);//获取该门店是否有导入商品，以便进行过滤导入商品
                if (CollectionUtils.isNotEmpty(importList)) {
                    prices = prices.stream().filter(p -> !importList.contains(p.getGoodsCode())).collect(Collectors.toList());
                }
                String platShopCode = shopMap.get(shop);//京东到家门店编号
                //获取商品市场价
                List<MarktPrice> marktPrices= getGoodsMarktPriceList(shop);
                Map<String,BigDecimal> marktMap=new HashMap<>();
                marktPrices.forEach(markt->marktMap.put(markt.getGoodCode(),markt.getMarktPrice()));

                prices.forEach(goodsPrice -> {
                    String sjGoodsCode = goodsPrice.getGoodsCode();//三江商品编号
                    try {
                        String platGoodsCode = platProdMap.get(sjGoodsCode);//京东商品编号
                        if (StringUtils.isEmpty(platGoodsCode)) {
                            log.error("商品：" + sjGoodsCode + ",在商品映射表中不存在！");
                            return;
                        }
                        BigDecimal price=new BigDecimal(goodsPrice.getSalePrice()).divide(new BigDecimal(100));
                        BigDecimal markt=price;
                        if(marktMap.containsKey(sjGoodsCode)){
                            if(marktMap.get(sjGoodsCode).compareTo(price)>0){
                                markt=marktMap.get(sjGoodsCode);
                            }
                        }
                        if(price.compareTo(BigDecimal.ZERO)<=0||markt.compareTo(BigDecimal.ZERO)<=0){
                            log.error("门店：" + shop + ",商品：" + sjGoodsCode + "市场价或门店价小于等于0,不能同步价格！");
                            return;
                        }

                        JDGoodsPriceItemObj item=new JDGoodsPriceItemObj();
                        item.setStationNo(platShopCode);
                        item.setSkuId(platGoodsCode);
                        item.setPrice(price.multiply(new BigDecimal(100)).longValue());
                        item.setMarketPrice(markt.multiply(new BigDecimal(100)).longValue());
                        goodsItemList.add(item);

                        JDBatchPriceSync update=new JDBatchPriceSync();
                        update.setGoodsid(new BigDecimal(sjGoodsCode));
                        update.setStoreid(shop);
                        update.setBatchmemberprice(BigDecimal.ZERO);
                        update.setBatchmarketprice(markt);
                        update.setBatchsaleprice(price);
                        updateList.add(update);

                    } catch (Exception ex) {
                        log.error("门店：" + shop + ",商品：" + sjGoodsCode + "全量同步价格错误：" + ex.toString());
                        return;
                    }
                });

            });
            batchUpdatePrice(goodsItemList, updateList);//批量更新价格

            //将同步价格的时间保存至redis,以便增量同步使用
            lastSyncPriceTiemOperations.set(RedisConstant.LAST_PRICE_SYNCJDDJ_TIME, Timestamp.valueOf(now));
        } catch (Exception ex) {
            log.error("增量同步门店价格出错：" + ex.toString());
        } finally {
            String end = simpleDateFormat.format(new Date());
            log.info(end + "-----增量同步门店价格---结束！");
        }

    }


    /**
     * 批量更新 京东到家价格、本地价格同步表数据
     *
     * @param goodsItemList 更新至京东到家的价格list
     * @param updateList    更新至本地价格同步表的库存list
     */
    public void batchUpdatePrice(List<JDGoodsPriceItemObj> goodsItemList, List<JDBatchPriceSync> updateList) {
        // 由于每次同步的商品有限制,所以要循环同步商品
        int size = goodsItemList.size();
        int batStockNum = jddjProperty.getBatchStockNum();
        int loopCount = (size + batStockNum - 1) / batStockNum;
        // 循环同步商品
        int endCount, startCount;
        for (int i = 0; i < loopCount; i++) {
            startCount = i * batStockNum;
            endCount = startCount + batStockNum >= size ? size : startCount + batStockNum;
            List<JDGoodsPriceItemObj> goodsItemListSub = goodsItemList.subList(startCount, endCount);
            List<JDBatchPriceSync> updateListSub = updateList.subList(startCount, endCount);//更新本地价格同步数据库List

            // 保证同步商品信息不为空
            if (CollectionUtils.isNotEmpty(goodsItemListSub)) {
                try {
                    //调用京东到家批量更新价格接口
                    JDBatchSyncPriceResponseObj jDReturnResult = jingdongUtil.callJingdongAPIToSYNCPrice(goodsItemListSub);
                    if (jDReturnResult.getSuccess() && jDReturnResult.getData().getCode().equals("0")) {
                        //京东端 如果获取批量更新流水成功，流水号有效期半小时
                        String batchNo = jDReturnResult.getData().getData().getBatchNo();
                        //调用流水号查询接口，并将流水信息保存至redis
                        String batchResult = common.queryBatch(batchNo);
                        JdBatchnoQueryResponse jdBatchnoQueryResponse = JsonUtil.jsonToObject(batchResult, JdBatchnoQueryResponse.class);
                        //把批量更新价格错误的流水添加至redis
                        if (jdBatchnoQueryResponse.getSuccess() == false || !jdBatchnoQueryResponse.getData().getCode().equals("0")) {
                            virStockBatchOperations.set(RedisConstant.ERROE_STOCK_BATCH_NO + batchNo, batchResult, 1, TimeUnit.DAYS);//redis保存1天
                        }

                        Date nowTime = new Date();
                        updateListSub.forEach(obj -> {
                            obj.setBatchno(batchNo);
                            obj.setSyncTime(nowTime);
                        });
                        bateUpdatePriceSyncJddj(updateListSub);//保存至京东到家价格同步表
                    }

                } catch (Exception ex) {
                    log.error("调用京东到家批量更新价格接口出错：" + ex.toString());
                    continue;
                }
            }
        }
    }

}
