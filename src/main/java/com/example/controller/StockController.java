package com.example.controller;


import com.example.business.model.ApiResult;
import com.example.business.opt.StockOpt;
import com.example.business.service.StockService;
import com.example.domain.sjhub.XtStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by wujianlong on 2017/3/30.
 */

@RestController
@RequestMapping("/jdstock")
public class StockController {

private static final Logger log= LoggerFactory.getLogger(StockController.class);


    @Autowired
    StockOpt stockOpt;

    @Autowired
    StockService stockService;


    /**
     * 虚拟库存同步任务
     */
    @RequestMapping(method = RequestMethod.GET, value = "/virstocksync")
    public void virStocksync() {
        stockOpt.virtualStockSync();
    }


    /**
     * 门店实际库存全量同步
     */
    @RequestMapping(method = RequestMethod.GET, value = "/allstocksync")
    public void allStockSync() {
        stockOpt.allStockSync();
    }

    /**
     * 门店实际库存增量同步
     */
    @RequestMapping(method = RequestMethod.GET, value = "/increstocksync")
    public void increStockSync() {
        stockOpt.increStockSync();
    }



    @RequestMapping(value="/releaseLockStockNum")
    public void releaseLockStockNum(){
        stockOpt.releaseLockStockNum();
    }



    @RequestMapping(value="/updateLockStock")
    public ApiResult updateLockStock(String storeId,String goodsId){
        ApiResult result=new ApiResult();
        if(StringUtils.isEmpty(storeId)||StringUtils.isEmpty(goodsId)){
            log.error("调用更新锁库存同步更新库存接口失败：请求参数门店编号或商品编号为空！");
            result.setMsg("调用更新锁库存同步更新库存接口失败：请求参数门店编号或商品编号为空！");
            return result;
        }
        result=stockService.updateLockStock(storeId,goodsId);

        return result;
    }

    @RequestMapping(value="/test")
    public void  test(String shop,String good){
        XtStore ss=stockOpt.getOneXtStockList(shop,good);
        System.out.print(ss);
    }

}
