package com.example.business.service;

import com.example.business.CommonJingdong.Common;
import com.example.business.model.ERPPromotionObj;
import com.example.utils.constant.RedisConstant;
import com.example.utils.property.PlatformProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wujianlong on 2017/3/27.
 */
@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);


    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, List<ERPPromotionObj>> valueOperations;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Map<String, String>> platProdOperations;

    @Autowired
    PlatformProperty platformProperty;

    @Autowired
    Common common;

    public String RefreshPrommotion() {
        String redisKey = "Promotion";
        try {
            valueOperations.set(redisKey, null);
            if(CollectionUtils.isEmpty(valueOperations.get(redisKey)))
            {
              log.info(simpleDateFormat.format(new Date())+" 已清空"+redisKey+" redis缓存");
            }
        } catch (Exception ex) {
            log.error(simpleDateFormat.format(new Date())+" 清空"+redisKey+" redis缓存失败："+ex.toString());
            return "0";
        }
        return "1";
    }


    public String refreshPlatProd() {
        String redisKey = RedisConstant.PLAT_PROD_MAP_SJ+platformProperty.getJddj();
        try {
            platProdOperations.set(redisKey, null);
            if(MapUtils.isEmpty(platProdOperations.get(redisKey)))
            {
                log.info(simpleDateFormat.format(new Date())+" 已清空"+redisKey+" redis缓存");
            }
        } catch (Exception ex) {
            log.error(simpleDateFormat.format(new Date())+" 清空"+redisKey+" redis缓存失败："+ex.toString());
            return "0";
        }
        return "1";
    }

}
