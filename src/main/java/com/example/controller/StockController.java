package com.example.controller;


import com.example.business.opt.StockOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by wujianlong on 2017/3/30.
 */

@RestController
@RequestMapping("/jdstock")
public class StockController {


    @Autowired
    StockOpt stockOpt;


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


}
