package com.example.controller;

import com.example.business.model.ApiResult;
import com.example.business.model.PriceSync;
import com.example.business.opt.PriceOpt;
import com.example.business.service.PriceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wujianlong on 2017/6/5.
 */
@RestController
@RequestMapping("/price")
public class PriceController {


    private static final Logger log = LoggerFactory.getLogger(PriceController.class);

    @Autowired
    PriceOpt priceOpt;


    @Autowired
    PriceService priceService;


    @RequestMapping(value = "/allsync", method = RequestMethod.GET)
    public void allPriceSync() {
        priceOpt.allPriceSync();
    }

    @RequestMapping(value = "/incrementalsync", method = RequestMethod.GET)
    public void incrementalPriceSync() {
        priceOpt.incrementalPriceSync();
    }


    /**
     * 中台修改价格
     * @param storeId
     * @param goodsId
     * @param price
     * @return
     */
    @RequestMapping(value = "/changePrice")
    public ApiResult changePrice(String storeId, String goodsId, BigDecimal price) {
        ApiResult result = new ApiResult();
        if (StringUtils.isEmpty(storeId) || StringUtils.isEmpty(goodsId) || price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("请求参数门店：" + storeId + "+商品：" + goodsId + "价格:" + price + "错误：参数为空或者价格小于等于0！");
            result.setMsg("请求参数错误：参数为空或者价格小于等于0！");
            return result;
        }
        result = priceService.changePrice(storeId, goodsId, price);
        return result;
    }

    /**
     * 导入商品价格
     * @param priceSyncList
     * @return
     */
    @RequestMapping(value="/importPrice",method=RequestMethod.POST)
    public ApiResult importPrice(@RequestBody List<PriceSync> priceSyncList){
        ApiResult result=new ApiResult();
        if(CollectionUtils.isEmpty(priceSyncList)){
            log.error("导入商品价格，请求参数为空");
            result.setMsg("导入商品价格，请求参数为空");
            return result;
        }
        result = priceService.importPrice(priceSyncList);
        return result;
    }



}
