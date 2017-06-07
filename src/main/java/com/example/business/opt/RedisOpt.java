package com.example.business.opt;

import com.example.business.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wujianlong on 2017/3/27.
 */
@Service
public class RedisOpt {


    @Autowired
    RedisService redisService;

    public String RefreshPrommotion() {
        return redisService.RefreshPrommotion();
    }

    public String refreshPlatProd(){
        return redisService.refreshPlatProd();
    }

}
