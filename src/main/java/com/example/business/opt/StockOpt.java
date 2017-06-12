package com.example.business.opt;

import com.example.business.BaseStockBusiness;
import com.example.business.CommonJingdong.Common;
import com.example.business.JingdongUtil;
import com.example.business.model.JDBatchStockSync;
import com.example.business.model.JDBatchSyncStockResponseObj;
import com.example.business.model.JDGoodsStockItemObj;
import com.example.business.model.JdBatchnoQueryResponse;
import com.example.domain.sjhub.*;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.repository.sjhub.StockSyncRepository;
import com.example.repository.sjhub.StockVirtualPlanRepository;
import com.example.repository.sjhub.StockVirtualSyncRepository;
import com.example.utils.JsonUtil;
import com.example.utils.constant.Constant;
import com.example.utils.constant.RedisConstant;
import com.example.utils.property.JddjProperty;
import com.example.utils.property.PlatformProperty;
import org.apache.commons.collections4.CollectionUtils;
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
 * Created by wujianlong on 2017/3/30.
 * 库存业务处理类
 */
@Service
public class StockOpt extends BaseStockBusiness {

    @Autowired
    StockVirtualPlanRepository stockVirtualPlanRepository;

    @Autowired
    StockVirtualSyncRepository stockVirtualSyncRepository;

    @Autowired
    PlatformProperty platformProperty;

    @Autowired
    PlatformShopRepository shopRepository;

    @Autowired
    StockSyncRepository stockSyncRepository;

    @Autowired
    protected StockVirtualPlanRepository planRepository;


    @Autowired
    JddjProperty jddjProperty;

    @Autowired
    JingdongUtil jingdongUtil;

    @Autowired
    Common common;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, String> virStockBatchOperations;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Timestamp> lastSyncStockTiemOperations;

    private static final Logger log = LoggerFactory.getLogger(StockOpt.class);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 虚拟商品库存同步(虚拟商品全量同步)
     */
    @Scheduled(cron = "0 */5 7-23 * * *")
    public void virtualStockSync() {

        String now = simpleDateFormat.format(new Date());
        log.info(now + "-----虚拟商品库存同步---开始！");

        try {
            List<StockVirtualPlan> stockVirtualPlanList = stockVirtualPlanRepository.findByVirStatusAndGoodsStatus(1, 1);//从虚拟计划表中获取虚拟商品
            if (CollectionUtils.isEmpty(stockVirtualPlanList)) {
                log.error("虚拟计划表中没有有效的虚拟计划，退出虚拟商品库存同步任务！");
                return;
            }
            List<StockVirtualSync> stockVirtualSyncList = stockVirtualSyncRepository.findAll();//从虚拟商品同步表获取所有虚拟商品信息
            if (CollectionUtils.isEmpty(stockVirtualSyncList)) {
                log.error("虚拟商品同步表中没有虚拟商品，退出虚拟商品库存同步任务！");
                return;
            }

            //虚拟计划Map(key:商场编号，val:该商场的所有商品编号)
            Map<String, List<String>> stockVirtualMap = new HashMap<>();
            for (StockVirtualPlan stockVirtualPlan :
                    stockVirtualPlanList) {
                groupByShopWithGoodList(stockVirtualMap, stockVirtualPlan.getSjShopCode(), stockVirtualPlan.getSjGoodsCode());
            }

            // 按门店分组数据
            Map<String, List<StockVirtualSync>> stockVirtualSyncListGroupByShip =
                    stockVirtualSyncList.stream()
                            .filter(stockVirtualSync -> stockVirtualSync != null)
                            .collect(Collectors.groupingBy(StockVirtualSync::getSjShopCode));


            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);
            //获取京东到家门店映射
            Map<String, String> platShopMap = getPlatShopMap(platformProperty.getJddj());


            List<JDGoodsStockItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchStockSync> updateList = new ArrayList<>();//更新本地库存同步数据库List

            stockVirtualMap.forEach((shopCode, goodsCodeList) -> {
                //PlatformShop shop = shopRepository.findBySjShopCodeAndPlatformIdAndStatus(shopCode, platformProperty.getJddj(), 1);
                String platShopCode = platShopMap.get(shopCode);//京东到家门店编号
                //如果中台没有该门店则跳过该门店虚拟库存同步
                if (StringUtils.isEmpty(platShopCode)) {
                    log.error("门店：" + shopCode + "不属于京东到家有效的门店，跳过该门店虚拟库存同步！");
                    return;
                }
                List<StockVirtualSync> stockVirtualSyncs = stockVirtualSyncListGroupByShip.get(shopCode);
                //如果虚拟库存同步表没有该门店则跳过该门店虚拟库存同步
                if (null == stockVirtualSyncs) {
                    log.error("虚拟库存同步表中没有门店：" + shopCode + "的同步信息，跳过该门店虚拟库存同步！");
                    return;
                }

                //筛选匹配的虚拟商品
                List<StockVirtualSync> stockVirtualSyncsMatch = stockVirtualSyncs.stream().filter(stockVirtualSync -> goodsCodeList.contains(stockVirtualSync.getSjGoodsCode())).collect(Collectors.toList());
                if (null == stockVirtualSyncsMatch) {
                    log.error("门店：" + shopCode + "未筛选出匹配的虚拟商品，跳过该门店虚拟库存同步！");
                    return;
                }

                //获取同步表该门店的所有商品
                List<StockSync> stockSyncList = stockSyncRepository.findByShopCode(shopCode);
                if (CollectionUtils.isEmpty(stockSyncList)) {
                    log.error("门店：" + shopCode + "同步表中没有该门店的数据，跳过该门店虚拟库存同步！");
                    return;
                }
                Map<String, StockSync> tempMap = new HashMap<>();
                stockSyncList.forEach(stockSync ->
                        tempMap.put(stockSync.getSjGoodsCode(), stockSync));


                stockVirtualSyncsMatch.forEach(matchVirStock -> {
                    String goodCode = matchVirStock.getSjGoodsCode();//三江商品编号
                    try {
                        StockSync stockSync = tempMap.get(goodCode);
                        if (null == stockSync) {
                            log.error("中台同步表中没有门店：" + shopCode + ",三江商品编号:" + goodCode + "的数据，跳过该商品虚拟库存同步！");
                            return;
                        }
                        String platGoodCode = platProdMap.get(goodCode);//京东到家商品编号
                        if (StringUtils.isEmpty(platGoodCode)) {
                            log.error("商品：" + goodCode + "在商品映射表中不存在京东到家的映射，跳过该商品虚拟库存同步！");
                            return;
                        }

                        int stockNum = matchVirStock.getVirtualStockNum() == null ? 0 : matchVirStock.getVirtualStockNum();//库存数
                        int lockNum = stockSync.getLockNum() == null ? 0 : stockSync.getLockNum();//锁库存
                        int bdyzNum = stockSync.getBdwmPreholdNum();//百度外卖预占库存
                        int tbyzNum = stockSync.getTbdjPreholdNum();//淘宝到家预占库存
                        int sjyzNum = stockSync.getSjdsPreholdNum();//三江网购预占库存
                        int validStock = stockNum - lockNum - bdyzNum - tbyzNum - sjyzNum;//有效库存：减去其他平台的预占库存
                        validStock = validStock <= 0 ? 0 : validStock;

                        JDGoodsStockItemObj goodsItem = new JDGoodsStockItemObj();
                        goodsItem.setCurrentQty(validStock);
                        goodsItem.setSkuId(Long.valueOf(platGoodCode));
                        goodsItem.setStationNo(platShopCode);
                        goodsItemList.add(goodsItem);

                        JDBatchStockSync update = new JDBatchStockSync();
                        update.setGoodsid(new BigDecimal(goodCode));
                        update.setStoreid(shopCode);
                        update.setStoreStock(new BigDecimal(String.valueOf(stockNum)));
                        update.setCurrentStock(new BigDecimal(String.valueOf(validStock)));
                        updateList.add(update);
                    } catch (Exception ex) {
                        log.error("商场:" + shopCode + ",商品:" + goodCode + "错误原因:" + ex.toString());
                        return;
                    }
                });
            });
            batchUpdateStock(goodsItemList, updateList);//批量更新库存

        } catch (Exception ex) {
            log.error("虚拟商品库存同步出错：" + ex.toString());
        } finally {
            String end = simpleDateFormat.format(new Date());
            log.info(end + "-----虚拟商品库存同步---结束！");
        }
    }


    /**
     * 门店实际库存--全量同步（虚拟库存另外同步）
     */
    @Scheduled(cron = "0 50 2 * * *")
    public void allStockSync() {
        String syncTime = simpleDateFormat.format(new Date());
        log.info(syncTime + "---------------全量同步门店实际库存---开始!");

        try {
            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);
            //获取京东到家门店list
            List<PlatformShop> platformShopList = shopRepository.findByPlatformIdAndStatus(platformProperty.getJddj(), 1);
            //获取有效的虚拟商品列表
            List<StockVirtualSync> stockVirtualSyncList = stockVirtualSyncRepository.getValidVirStock();
            //将有效的虚拟商品列表根据商场编号存入map--key:商场编号，value:商品编号list
            Map<String, List<String>> virStockGroupByShopMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(stockVirtualSyncList)) {
                stockVirtualSyncList.forEach(obj ->
                        groupByShopWithGoodList(virStockGroupByShopMap, obj.getSjShopCode(), obj.getSjGoodsCode())
                );
            }

            List<JDGoodsStockItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchStockSync> updateList = new ArrayList<>();//更新本地库存同步数据库List

            //根据门店来循环同步库存
            platformShopList.forEach(obj -> {
                String sjShop = obj.getSjShopCode();
                log.info("门店：" + sjShop + "全量同步库存开始！");
                try {
                    //获取可用的中间库存list
                    List<XtStore> storeList = this.getXtStockList(sjShop);
                    if (CollectionUtils.isEmpty(storeList)) {
                        log.error("门店：" + sjShop + "没有可用的中间库存！");
                        return;
                    } else {
                        storeList = storeList.stream().filter(store -> store != null).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(storeList)) {
                            log.error("门店：" + sjShop + "没有可用的中间库存！");
                            return;
                        }
                    }

                    //取得指定门店可同步的库存同步信息list
                    List<StockSync> stockSyncList = stockSyncRepository.findByShopCodeAndSyncFlag(sjShop, 1);
                    if (CollectionUtils.isEmpty(stockSyncList)) {
                        log.error("门店：" + sjShop + "没有可同步的库存！");
                        return;
                    }
                    Map<String, StockSync> stockSyncMap = new HashMap<>();
                    stockSyncList.forEach(stockSync -> {
                                if (stockSync != null)
                                    stockSyncMap.put(stockSync.getSjGoodsCode(), stockSync);
                            }
                    );
                    List<String> virStockList = virStockGroupByShopMap.get(sjShop);//获取该门店是否有虚拟商品，以便进行过滤虚拟商品
                    if (CollectionUtils.isNotEmpty(virStockList)) {
                        storeList = storeList.stream().filter(store -> !virStockList.contains(store.getSjGoodsCode())).collect(Collectors.toList());
                    }
                    String platShopCode = obj.getPlatformShopCode();//京东到家门店编号
                    //以中间库为核心，循环同步库存
                    storeList.forEach(store -> {
                        // InsertGoodList(platProdMap, goodsItemList, updateList, sjShop, stockSyncMap, platShopCode, store);
                        String sjGoodsCode = store.getSjGoodsCode();//三江商品编号
                        try {
                            String platGoodsCode = platProdMap.get(sjGoodsCode);//京东商品编号
                            if (StringUtils.isEmpty(platGoodsCode)) {
                                log.error("商品：" + sjGoodsCode + ",在商品映射表中不存在！");
                                return;
                            }
                            StockSync stockSync = stockSyncMap.get(sjGoodsCode);//获取中台的库存同步表商品信息
                            if (null == stockSync) {
                                log.error("中台同步表中没有门店：" + sjShop + ",三江商品编号:" + sjGoodsCode + "的数据(可能为不同步状态)，跳过该商品全量库存同步！");
                                return;
                            }

                            int stockNum = store.getStockNumber() <= 0 ? 0 : store.getStockNumber();//中间库库存数量
                            int lockNum = stockSync.getLockNum() == null ? 0 : stockSync.getLockNum();//锁库存
                            int bdyzNum = stockSync.getBdwmPreholdNum();//百度外卖预占库存
                            int tbyzNum = stockSync.getTbdjPreholdNum();//淘宝到家预占库存
                            int sjyzNum = stockSync.getSjdsPreholdNum();//三江网购预占库存
                            int validStock = stockNum - lockNum - bdyzNum - tbyzNum - sjyzNum;//有效库存：减去其他平台的预占库存
                            validStock = validStock <= 0 ? 0 : validStock;

                            JDGoodsStockItemObj goodsItem = new JDGoodsStockItemObj();
                            goodsItem.setCurrentQty(validStock);
                            goodsItem.setSkuId(Long.valueOf(platGoodsCode));
                            goodsItem.setStationNo(platShopCode);
                            goodsItemList.add(goodsItem);

                            JDBatchStockSync update = new JDBatchStockSync();
                            update.setGoodsid(new BigDecimal(sjGoodsCode));
                            update.setStoreid(sjShop);
                            update.setStoreStock(new BigDecimal(String.valueOf(stockNum)));
                            update.setCurrentStock(new BigDecimal(String.valueOf(validStock)));
                            updateList.add(update);
                        } catch (Exception ex) {
                            log.error("门店：" + sjShop + ",商品：" + sjGoodsCode + "全量同步库存错误：" + ex.toString());
                            return;
                        }
                    });

                } catch (Exception ex) {
                    log.error("门店：" + sjShop + "全量同步库存错误:" + ex.toString());
                }
                log.info("门店：" + sjShop + "全量同步库存结束！");
            });

            batchUpdateStock(goodsItemList, updateList);//批量更新库存

            //将同步库存的时间保存至redis,以便增量同步使用
            lastSyncStockTiemOperations.set(RedisConstant.LAST_STOCK_SYNCJDDJ_TIME, Timestamp.valueOf(syncTime));
        } catch (Exception ex) {
            log.error("全量同步门店实际库存出错：" + ex.toString());
        } finally {
            String end = simpleDateFormat.format(new Date());
            log.info(end + "-----全量同步门店实际库存---结束！");
        }

    }


    /**
     * 门店实际库存--增量同步
     */
    @Scheduled(cron = "0 */5 7-23 * * *")
    public void increStockSync() {
        LocalDateTime now = LocalDateTime.now();
        String start = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info(start + "---------------增量同步门店实际库存---开始!");

        try {
            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);

            List<PlatformShop> platformShopList = shopRepository.findByPlatformIdAndStatus(platformProperty.getJddj(), Constant.SHOP_STATUS_ENABLE);
            if (CollectionUtils.isEmpty(platformShopList)) {
                log.error("中台没有京东到家的有效商场！");
                return;
            }
            List<String> shopList = new ArrayList<>();//用来sql查询的商场列表
            Map<String, String> shopMap = new HashMap<>(); //获取京东到家商场映射
            platformShopList.forEach(shop -> {
                shopList.add(shop.getSjShopCode());
                shopMap.put(shop.getSjShopCode(), shop.getPlatformShopCode());
            });

            List<XtStore> xtStoreList = this.getXtStockList(shopList, now);//取得京东到家所有门店的更新的商品数据列表
            if (CollectionUtils.isEmpty(xtStoreList)) {
                log.error("中间库没有可增量库存同步的商品！");
                return;
            } else {
                xtStoreList = xtStoreList.stream()
                        .filter(xtStore -> xtStore != null)
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(xtStoreList)) {
                    log.error("中间库没有可增量库存同步的商品！");
                    return;
                }
            }
            Map<String, List<XtStore>> xtStoreGroupByShop
                    = xtStoreList.stream()
                    .collect(Collectors.groupingBy(XtStore::getSjShopCode));

            //获取有效的虚拟商品列表
            List<StockVirtualSync> stockVirtualSyncList = stockVirtualSyncRepository.getValidVirStock();
            //将有效的虚拟商品列表根据商场编号存入map--key:商场编号，value:商品编号list
            Map<String, List<String>> virStockGroupByShopMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(stockVirtualSyncList)) {
                stockVirtualSyncList.forEach(obj ->
                        groupByShopWithGoodList(virStockGroupByShopMap, obj.getSjShopCode(), obj.getSjGoodsCode())
                );
            }

            List<JDGoodsStockItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchStockSync> updateList = new ArrayList<>();//更新本地库存同步数据库List

            xtStoreGroupByShop.forEach((shop, xtList) -> {
                log.info("门店"+shop+"开始增量库存同步！");
                //取得指定门店可同步的库存同步信息list
                List<StockSync> stockSyncList = stockSyncRepository.findByShopCodeAndSyncFlag(shop, 1);
                if (CollectionUtils.isEmpty(stockSyncList)) {
                    log.error("门店：" + shop + "没有可同步的库存！");
                    return;
                }
                Map<String, StockSync> stockSyncMap = new HashMap<>();
                stockSyncList.forEach(stockSync -> {
                            if (stockSync != null)
                                stockSyncMap.put(stockSync.getSjGoodsCode(), stockSync);
                        }
                );
                List<String> virStockList = virStockGroupByShopMap.get(shop);//获取该门店是否有虚拟商品，以便进行过滤虚拟商品
                if (CollectionUtils.isNotEmpty(virStockList)) {
                    xtList = xtList.stream().filter(store -> !virStockList.contains(store.getSjGoodsCode())).collect(Collectors.toList());
                }
                String platShopCode = shopMap.get(shop);//京东到家门店编号

                xtList.forEach(store -> {
                    // InsertGoodList(platProdMap, goodsItemList, updateList, shop, stockSyncMap, platShopCode, store);
                    String sjGoodsCode = store.getSjGoodsCode();//三江商品编号
                    try {
                        String platGoodsCode = platProdMap.get(sjGoodsCode);//京东商品编号
                        if (StringUtils.isEmpty(platGoodsCode)) {
                            log.error("商品：" + sjGoodsCode + ",在商品映射表中不存在！");
                            return;
                        }
                        StockSync stockSync = stockSyncMap.get(sjGoodsCode);//获取中台的库存同步表商品信息
                        if (null == stockSync) {
                            log.error("中台同步表中没有门店：" + shop + ",三江商品编号:" + sjGoodsCode + "的数据(可能为不同步状态)，跳过该商品全量库存同步！");
                            return;
                        }

                        int stockNum = store.getStockNumber() <= 0 ? 0 : store.getStockNumber();//中间库库存数量
                        int lockNum = stockSync.getLockNum() == null ? 0 : stockSync.getLockNum();//锁库存
                        int bdyzNum = stockSync.getBdwmPreholdNum();//百度外卖预占库存
                        int tbyzNum = stockSync.getTbdjPreholdNum();//淘宝到家预占库存
                        int sjyzNum = stockSync.getSjdsPreholdNum();//三江网购预占库存
                        int validStock = stockNum - lockNum - bdyzNum - tbyzNum - sjyzNum;//有效库存：减去其他平台的预占库存
                        validStock = validStock <= 0 ? 0 : validStock;

                        JDGoodsStockItemObj goodsItem = new JDGoodsStockItemObj();
                        goodsItem.setCurrentQty(validStock);
                        goodsItem.setSkuId(Long.valueOf(platGoodsCode));
                        goodsItem.setStationNo(platShopCode);
                        goodsItemList.add(goodsItem);

                        JDBatchStockSync update = new JDBatchStockSync();
                        update.setGoodsid(new BigDecimal(sjGoodsCode));
                        update.setStoreid(shop);
                        update.setStoreStock(new BigDecimal(String.valueOf(stockNum)));
                        update.setCurrentStock(new BigDecimal(String.valueOf(validStock)));
                        updateList.add(update);
                    } catch (Exception ex) {
                        log.error("门店：" + shop + ",商品：" + sjGoodsCode + "全量同步库存错误：" + ex.toString());
                        return;
                    }
                });

            });
            batchUpdateStock(goodsItemList, updateList);//批量更新库存

            //将同步库存的时间保存至redis,以便增量同步使用
            lastSyncStockTiemOperations.set(RedisConstant.LAST_STOCK_SYNCJDDJ_TIME, Timestamp.valueOf(now));
        } catch (Exception ex) {
            log.error("增量同步门店实际库存出错：" + ex.toString());
        } finally {
            String end = simpleDateFormat.format(new Date());
            log.info(end + "-----增量同步门店实际库存---结束！");
        }

    }

    public void InsertGoodList(Map<String, String> platProdMap, List<JDGoodsStockItemObj> goodsItemList, List<JDBatchStockSync> updateList, String shop, Map<String, StockSync> stockSyncMap, String platShopCode, XtStore store) {
        String sjGoodsCode = store.getSjGoodsCode();//三江商品编号
        try {
            String platGoodsCode = platProdMap.get(sjGoodsCode);//京东商品编号
            if (StringUtils.isEmpty(platGoodsCode)) {
                log.error("商品：" + sjGoodsCode + ",在商品映射表中不存在！");
                return;
            }
            StockSync stockSync = stockSyncMap.get(sjGoodsCode);//获取中台的库存同步表商品信息
            if (null == stockSync) {
                log.error("中台同步表中没有门店：" + shop + ",三江商品编号:" + sjGoodsCode + "的数据(可能为不同步状态)，跳过该商品全量库存同步！");
                return;
            }

            int stockNum = store.getStockNumber() <= 0 ? 0 : store.getStockNumber();//中间库库存数量
            int lockNum = stockSync.getLockNum() == null ? 0 : stockSync.getLockNum();//锁库存
            int bdyzNum = stockSync.getBdwmPreholdNum();//百度外卖预占库存
            int tbyzNum = stockSync.getTbdjPreholdNum();//淘宝到家预占库存
            int sjyzNum = stockSync.getSjdsPreholdNum();//三江网购预占库存
            int validStock = stockNum - lockNum - bdyzNum - tbyzNum - sjyzNum;//有效库存：减去其他平台的预占库存

            JDGoodsStockItemObj goodsItem = new JDGoodsStockItemObj();
            goodsItem.setCurrentQty(validStock);
            goodsItem.setSkuId(Long.valueOf(platGoodsCode));
            goodsItem.setStationNo(platShopCode);
            goodsItemList.add(goodsItem);

            JDBatchStockSync update = new JDBatchStockSync();
            update.setGoodsid(new BigDecimal(sjGoodsCode));
            update.setStoreid(shop);
            update.setStoreStock(new BigDecimal(String.valueOf(stockNum)));
            update.setCurrentStock(new BigDecimal(String.valueOf(validStock)));
            updateList.add(update);
        } catch (Exception ex) {
            log.error("门店：" + shop + ",商品：" + sjGoodsCode + "全量同步库存错误：" + ex.toString());
            return;
        }
    }

    /**
     * 锁定库存解锁
     */
    @Scheduled(cron = "0 0,30 * * * *")
    public void releaseLockStockNum() {
        LocalDateTime now = LocalDateTime.now();
        // String initTime = String.format("%02d", now.getHour()) + ":" + String.format("%02d", now.getMinute());
        String initTime = "19:30";
        try {
            // 取得当前时间的释放锁定库存计划
            List<StockVirtualPlan> planList = planRepository.findByVirLockSyncTimeAndVirLockStatus(initTime, Constant.SYNC_FLAG_ENABLE);
            if (CollectionUtils.isEmpty(planList)) {
                return;
            }

            log.info("释放锁定库存数据releaseLockStockNum........................start");

            Map<String, List<StockVirtualPlan>> plaMap = planList.stream().collect(Collectors.groupingBy(StockVirtualPlan::getSjShopCode));
            //获取京东到家商品映射
            Map<String, String> platProdMap = getPlatProdMap(platformProperty.getJddj(), true);

            List<PlatformShop> platformShopList = shopRepository.findByPlatformIdAndStatus(platformProperty.getJddj(), Constant.SHOP_STATUS_ENABLE);
            if (CollectionUtils.isEmpty(platformShopList)) {
                log.error("中台没有京东到家的有效商场！");
                return;
            }
            Map<String, String> shopMap = new HashMap<>(); //获取京东到家商场映射
            platformShopList.forEach(shop ->
                    shopMap.put(shop.getSjShopCode(), shop.getPlatformShopCode())
            );

            //获取有效的虚拟商品列表
            List<StockVirtualSync> stockVirtualSyncList = stockVirtualSyncRepository.getValidVirStock();
            Map<String, List<StockVirtualSync>> virMap = stockVirtualSyncList.stream().collect(Collectors.groupingBy(StockVirtualSync::getSjShopCode));

            List<JDGoodsStockItemObj> goodsItemList = new ArrayList<>();//同步至京东到家的商品List
            List<JDBatchStockSync> updateList = new ArrayList<>();//更新本地库存同步数据库List

            plaMap.forEach((shop, lockList) -> {
                String jdShop = shopMap.get(shop);
                if (null == jdShop) {
                    log.error("门店" + shop + "不是京东到家有效门店！");
                    return;
                }
                Map<String, Integer> virGoodMap = new HashMap<>();
                List<StockVirtualSync> virList = virMap.get(shop);
                if (CollectionUtils.isNotEmpty(virList)) {
                    virList.forEach(v -> virGoodMap.put(v.getSjGoodsCode(), v.getVirtualStockNum()));
                }
                //获取可用的中间库存list
                List<XtStore> storeList = this.getXtStockList(shop);
                if (CollectionUtils.isEmpty(storeList)) {
                    log.error("门店：" + shop + "没有可用的中间库存！");
                    // return;
                } else {
                    storeList = storeList.stream().filter(store -> store != null).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(storeList)) {
                        log.error("门店：" + shop + "没有可用的中间库存！");
                        // return;
                    }
                }
                Map<String, Integer> xtGoodMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(storeList)) {
                    storeList.forEach(store -> xtGoodMap.put(store.getSjGoodsCode(), store.getStockNumber()));
                }

                Map<String, StockSync> stockSyncMap = new HashMap<>();
                //取得指定门店所有的库存同步信息list
                List<StockSync> stockSyncList = stockSyncRepository.findByShopCode(shop);
                if (CollectionUtils.isNotEmpty(stockSyncList)) {
                    stockSyncList.forEach(stockSync ->
                            stockSyncMap.put(stockSync.getSjGoodsCode(), stockSync)
                    );
                }
                lockList.forEach(lock -> {
                    String sjGood = lock.getSjGoodsCode();
                    String jdGood = platProdMap.get(sjGood);
                    if (null == jdGood) {
                        log.error("商品：" + sjGood + "不是京东到家商品！");
                        return;
                    }
                    Integer stockNum = 0;
                    if (virGoodMap.containsKey(sjGood)) {
                        stockNum = virGoodMap.get(sjGood);
                    } else {
                        if (xtGoodMap.containsKey(sjGood)) {
                            stockNum = xtGoodMap.get(sjGood);
                        } else {
                            log.error("虚拟库存和中间库不存在有效的商场" + shop + "商品" + sjGood);
                            return;
                        }
                    }

                    StockSync stockSync = stockSyncMap.get(sjGood);
                    if (null == stockSync) {
                        log.error("中台同步表中没有门店：" + shop + ",三江商品编号:" + sjGood + "的数据");
                        return;
                    }
                    if (!virGoodMap.containsKey(sjGood) && stockSync.getSyncFlag() == 0) {
                        log.error("中台同步表中门店：" + shop + ",三江商品编号:" + sjGood + "为不同步状态，并且不为虚拟商品，所以不进行库存同步！");
                        return;
                    }

                    int lockNum = 0;//锁定库存当做已清零处理
                    int bdyzNum = stockSync.getBdwmPreholdNum();//百度外卖预占库存
                    int tbyzNum = stockSync.getTbdjPreholdNum();//淘宝到家预占库存
                    int sjyzNum = stockSync.getSjdsPreholdNum();//三江网购预占库存
                    int validStock = stockNum - lockNum - bdyzNum - tbyzNum - sjyzNum;//有效库存：减去其他平台的预占库存
                    validStock = validStock <= 0 ? 0 : validStock;

                    JDGoodsStockItemObj goodsItem = new JDGoodsStockItemObj();
                    goodsItem.setCurrentQty(validStock);
                    goodsItem.setSkuId(Long.valueOf(jdGood));
                    goodsItem.setStationNo(jdShop);
                    goodsItemList.add(goodsItem);

                    JDBatchStockSync update = new JDBatchStockSync();
                    update.setGoodsid(new BigDecimal(jdGood));
                    update.setStoreid(shop);
                    update.setStoreStock(new BigDecimal(String.valueOf(stockNum)));
                    update.setCurrentStock(new BigDecimal(String.valueOf(validStock)));
                    updateList.add(update);

                });
            });
            batchUpdateStock(goodsItemList, updateList);//批量更新库存

        } catch (Exception ex) {
            log.error("释放锁定库存失败：" + ex.toString());
        } finally {
            String end = simpleDateFormat.format(new Date());
            log.info(end + "-----释放锁定库存数据releaseLockStockNum---结束！");
        }
    }


    /**
     * 批量更新 京东到家库存、本地库存同步表数据
     *
     * @param goodsItemList 更新至京东到家的库存list
     * @param updateList    更新至本地库存同步表的库存list
     */
    public void batchUpdateStock(List<JDGoodsStockItemObj> goodsItemList, List<JDBatchStockSync> updateList) {
        // 由于每次同步的商品有限制,所以要循环同步商品
        int size = goodsItemList.size();
        int batStockNum = jddjProperty.getBatchStockNum();
        int loopCount = (size + batStockNum - 1) / batStockNum;
        // 循环同步商品
        int endCount, startCount;
        for (int i = 0; i < loopCount; i++) {
            startCount = i * batStockNum;
            endCount = startCount + batStockNum >= size ? size : startCount + batStockNum;
            List<JDGoodsStockItemObj> goodsItemListSub = goodsItemList.subList(startCount, endCount);
            List<JDBatchStockSync> updateListSub = updateList.subList(startCount, endCount);//更新本地库存同步数据库List

            // 保证同步商品信息不为空
            if (CollectionUtils.isNotEmpty(goodsItemListSub)) {
                try {
                    //调用京东到家批量更新库存接口
                    JDBatchSyncStockResponseObj jDReturnResult = jingdongUtil.callJingdongAPIToSYNCStock(goodsItemListSub);
                    if (jDReturnResult.getSuccess() && jDReturnResult.getData().getCode().equals("0")) {
                        //京东端 如果获取批量更新流水成功，流水号有效期半小时
                        String batchNo = jDReturnResult.getData().getData().getBatchNo();
                        //调用流水号查询接口，并将流水信息保存至redis
                        String batchResult = common.queryBatch(batchNo);
                        JdBatchnoQueryResponse jdBatchnoQueryResponse = JsonUtil.jsonToObject(batchResult, JdBatchnoQueryResponse.class);
                        //把批量更新库存错误的流水添加至redis
                        if (jdBatchnoQueryResponse.getSuccess() == false || !jdBatchnoQueryResponse.getData().getCode().equals("0")) {
                            virStockBatchOperations.set(RedisConstant.ERROE_STOCK_BATCH_NO + batchNo, batchResult, 1, TimeUnit.DAYS);//redis保存1天
                        }

                        Date nowTime = new Date();
                        updateListSub.forEach(obj -> {
                            obj.setBatchno(batchNo);
                            obj.setSyncTime(nowTime);
                        });
                        bateUpdateStockSyncJddj(updateListSub);//保存至京东到家库存同步表

                    }

                } catch (Exception ex) {
                    log.error("调用京东到家批量更新库存接口出错：" + ex.toString());
                    continue;
                }
            }
        }
    }


    /**
     * 将相同商场的商品存入同一个list中，并存入map中
     *
     * @param stockVirtualMap
     * @param shopCode        商场编号
     * @param goodsCode       商品编号
     */
    public void groupByShopWithGoodList(Map<String, List<String>> stockVirtualMap, String shopCode, String goodsCode) {
        if (stockVirtualMap.containsKey(shopCode)) {
            stockVirtualMap.get(shopCode).add(goodsCode);
        } else {
            List<String> listStr = new ArrayList<>();
            listStr.add(goodsCode);
            stockVirtualMap.put(shopCode, listStr);
        }
    }

}
