package com.example.controller;

import com.example.business.CommonJingdong.Common;
import com.example.business.model.ApiResult;
import com.example.domain.jddj.ApiToken;
import com.example.repository.jddj.ApiTokenRepository;
import com.example.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wujianlong on 2017/6/14.
 */
@RestController
public class ReceiveController {

    private static final Logger log= LoggerFactory.getLogger(ReceiveController.class);

    @Autowired
    ApiTokenRepository apiTokenRepository;

    @Autowired
    Common common;

    /**
     * 获取token
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/getToken")
    public ApiResult getToken(String token) {
        ApiResult result=new ApiResult();
        if (StringUtils.isEmpty(token)) {
            log.error("获取token失败：请求token参数为空！");
            result.setMsg("获取token失败：请求token参数为空！");
            return result;
        }
        try {
            ApiToken apiToken = JsonUtil.jsonToObject(token, ApiToken.class);
            apiTokenRepository.save(apiToken);
            result.setCode("0");
            result.setSuccess(true);
        }
        catch (Exception ex){
            log.error("获取token失败："+ex.toString());
            result.setMsg("获取token失败，请重试！");
            return result;
        }
        return result;
    }
}
