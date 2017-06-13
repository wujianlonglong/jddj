package com.example.business.service;

import com.example.business.BaseStockBusiness;
import com.example.business.JingdongUtil;
import com.example.business.model.ApiResult;
import com.example.business.model.JDSingleStockUpdateResponseObj;
import com.example.domain.sjhub.PlatformShop;
import com.example.domain.sjhub.StockSync;
import com.example.domain.sjhub.StockVirtualSync;
import com.example.domain.sjhub.XtStore;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.repository.sjhub.StockSyncRepository;
import com.example.repository.sjhub.StockVirtualSyncRepository;
import com.example.utils.property.PlatformProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wujianlong on 2017/6/9.
 */

@Service
public class StockService {

    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    @Autowired
    PlatformShopRepository platformShopRepository;

    @Autowired
    PlatformProperty platformProperty;

    @Autowired
    StockVirtualSyncRepository stockVirtualSyncRepository;


    @Autowired
    StockSyncRepository stockSyncRepository;

    @Autowired
    BaseStockBusiness baseStockBusiness;

    @Autowired
    JingdongUtil jingdongUtil;

    public ApiResult updateLockStock(String storeId, String goodsId) {
        ApiResult result = new ApiResult();
        try {
            PlatformShop platformShop = platformShopRepository.findBySjShopCodeAndPlatformIdAndStatus(storeId,platformProperty.getJddj(), 1);
            if (null == platformShop) {
                log.error("调用更新锁库存同步更新库存接口失败，商场" + storeId + "不是京东到家商场！");
                result.setMsg("调用更新锁库存同步更新库存接口失败，商场" + storeId + "不是京东到家商场！");
                return result;
            }
            String jdShop = platformShop.getPlatformShopCode();

            //获取京东到家商品映射
            Map<String, String> platProdMap = baseStockBusiness.getPlatProdMap(platformProperty.getJddj(), true);
            String jdgood = platProdMap.get(goodsId);
            if (StringUtils.isEmpty(jdgood)) {
                log.error("调用更新锁库存同步更新库存接口失败,商品" + goodsId + "不是京东到家商品！");
                result.setMsg("调用更新锁库存同步更新库存接口失败,商品" + goodsId + "不是京东到家商品！");
                return result;
            }

            Integer stockNum = 0;
            int status=0;
            StockVirtualSync stockVirtualSync = stockVirtualSyncRepository.getValidVirStockByShopCodeAndGoodCode(storeId, goodsId);
            if (null != stockVirtualSync) {
                stockNum = stockVirtualSync.getVirtualStockNum();
            } else {
                XtStore xtStore = baseStockBusiness.getOneXtStockList(storeId, goodsId);
                if (null != xtStore) {
                    stockNum = xtStore.getStockNumber();
                    status=xtStore.getStatus();
                } else {
                    log.error("中间库未找到有效商品：商场编号：" + storeId + "管理编号：" + goodsId);
                    result.setMsg("中间库未找到有效商品：商场编号：" + storeId + "管理编号：" + goodsId);
                    return result;
                }
            }

            StockSync stockSync = stockSyncRepository.findByShopCodeAndSjGoodsCode(storeId, goodsId);
            if (null == stockSync) {
                log.error("中台同步表中没有门店：" + storeId + ",商品:" + goodsId + "的数据！");
                result.setMsg("中台同步表中没有门店：" + storeId + ",商品:" + goodsId + "的数据！");
                return result;
            }
            //非虚拟商品，状态为不同步的不进行库存同步
            if (stockSync.getSyncFlag() == 0 && stockVirtualSync == null) {
                log.error("门店：" + storeId + ",商品:" + goodsId + "状态为不同步，并且不是虚拟商品，所以不进行库存同步！");
                result.setMsg("门店：" + storeId + ",商品:" + goodsId + "状态为不同步，并且不是虚拟商品，所以不进行库存同步！");
                return result;
            }

            int lockNum = stockSync.getLockNum() == null ? 0 : stockSync.getLockNum();//锁库存
            int bdyzNum = stockSync.getBdwmPreholdNum();//百度外卖预占库存
            int tbyzNum = stockSync.getTbdjPreholdNum();//淘宝到家预占库存
            int sjyzNum = stockSync.getSjdsPreholdNum();//三江网购预占库存
            int validStock = stockNum - lockNum - bdyzNum - tbyzNum - sjyzNum;//有效库存：减去其他平台的预占库存
            validStock = validStock <= 0 ? 0 : validStock;

            JDSingleStockUpdateResponseObj jdResult = jingdongUtil.UpdateCurrentQty(jdShop, jdgood, validStock);


            if (jdResult.getSuccess()) {
                if (jdResult.getData().getRetCode().equals("0")) {
                    //京东端 如果刷新成功
                    int excute = baseStockBusiness.UpdateGoodsStockSync(storeId, goodsId, stockNum, validStock, status);
                    if (excute > 0) {
                        result.setSuccess(true);
                        result.setCode("0");
                        result.setMsg("调用京东到家更新库存接口成功！京东到家现货库存为：" + validStock);
                    } else {
                        log.error("更新京东到家现货库存为：" + validStock + "成功，更新本地数据库失败(可能本地没有该条数据):商场" + storeId + "商品" + goodsId+"库存"+stockNum+"现货库存"+validStock+"状态"+status);
                        result.setMsg("更新京东到家现货库存为：" + validStock + "成功，更新本地数据库失败(可能本地没有该条数据):商场" + storeId + "商品" + goodsId+"库存"+stockNum+"现货库存"+validStock+"状态"+status);
                        return result;
                    }
                } else {
                    log.error("门店" + jdShop + "商品" + jdgood + "库存" + validStock + "调用京东到家更新库存接口失败:" + jdResult.getData().getRetMsg());
                    result.setMsg("门店" + jdShop + "商品" + jdgood + "库存" + validStock + "调用京东到家更新库存接口失败:" + jdResult.getData().getRetMsg());
                    return result;
                }
            } else {
                log.error("门店" + storeId + "商品" + jdgood + "库存" + validStock + "调用京东到家更新库存接口失败:" + jdResult.getMsg());
                result.setMsg("门店" + storeId + "商品" + jdgood + "库存" + validStock + "调用京东到家更新库存接口失败:" + jdResult.getMsg());
                return result;
            }
        } catch (Exception ex) {
            log.error("商场：" + storeId + "商品" + goodsId + "更新库存失败：" + ex.toString());
            result.setMsg("商场：" + storeId + "商品" + goodsId + "更新库存失败：" + ex.toString());
            result.setSuccess(false);
            result.setCode("-1");
            return result;
        }
        return result;
    }

}
