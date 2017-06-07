package com.example.controller;

import com.example.business.opt.RedisOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wujianlong on 2017/3/27.
 */

@RestController
@RequestMapping("/jdredis")
public class RedisController {


    @Autowired
    RedisOpt redisOpt;


    /**
     * 清空promotion的 redis缓存数据
     *
     * @return "0":清空失败 ;"1":清空成功
     */
    @RequestMapping(method = RequestMethod.GET, value = "/refreshprommotion")
    public String RefreshPrommotion() {
        return redisOpt.RefreshPrommotion();
    }


    /**
     * 清空京东到家商品映射的 redis缓存数据
     *
     * @return "0":清空失败 ;"1":清空成功
     */
    @RequestMapping(method = RequestMethod.GET, value = "/refreshplatprod")
    public String refreshPlatProd() {
        return redisOpt.refreshPlatProd();
    }


}
