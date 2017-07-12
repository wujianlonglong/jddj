package com.example.business;

import com.example.business.CommonJingdong.Common;
import com.example.business.model.*;
import com.example.utils.JsonUtil;
import com.example.utils.property.JddjProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Convert;
import javax.xml.ws.spi.http.HttpHandler;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by wujianlong on 2016/12/23.
 */

@Component
public class JingdongUtil {

    private static final Logger log = LoggerFactory.getLogger(JingdongUtil.class);

    @Autowired
    JddjProperty jddjProperty;


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Common common;

    public JDBatchSyncStockResponseObj callJingdongAPIToSYNCStock(List<JDGoodsStockItemObj> tmpjDGoodsStockItemObjList) throws SQLException, ClassNotFoundException, InterruptedException, ParseException {
        int num = 0;//反复调用京东接口次数
        JDBatchSyncStockResponseObj result = null;
        do {
            num++;
            JDBatchSyncStockRequestObj jDBatchSyncStockRequestObj = new JDBatchSyncStockRequestObj();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());

            jDBatchSyncStockRequestObj.getJd_param_json().setGoodsItemList(tmpjDGoodsStockItemObjList);
            String token = common.getNewToken();

            String sign = common.GetRequestSign(JsonUtil.objectToString(jDBatchSyncStockRequestObj.getJd_param_json()), currentTime, "1.0", token);

            jDBatchSyncStockRequestObj.setSign(sign);

            String postData = "v=" + jddjProperty.getV()
                    + "&format=" + jddjProperty.getFormat()
                    + "&app_key=" + jddjProperty.getApp_key()
                    + "&app_secret=" + jddjProperty.getApp_secret()
                    + "&token=" + jddjProperty.getToken()
                    + "&jd_param_json=" + JsonUtil.objectToString(jDBatchSyncStockRequestObj.getJd_param_json())
                    + "&sign=" + jDBatchSyncStockRequestObj.getSign()
                    + "&timestamp=" + currentTime;


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<String>(postData, headers);

            String requestTime = dateFormat.format(new Date());
            long apiMillis = System.currentTimeMillis();
            //log.info("准备调用京东端批量更新库存接口：" + jddjProperty.getApiUrl() + "/stock/batchUpdate" + ",数据数量：" + tmpjDGoodsStockItemObjList.size() + ",请求次数：" + num + ",请求数据：" + postData + ",请求时间：" + requestTime);
            log.info("准备调用京东端批量更新库存接口：" + jddjProperty.getApiUrl() + "/stock/batchUpdate" + ",数据数量：" + tmpjDGoodsStockItemObjList.size() + ",请求次数：" + num + ",请求时间：" + requestTime);

            String Response = restTemplate.postForObject(jddjProperty.getApiUrl() + "/stock/batchUpdate", entity, String.class);
            Response = common.getClearJsonString(Response);

            log.info("京东端批量更新库存接口返回数据：" + Response + ",耗时：" + (System.currentTimeMillis() - apiMillis));
            result = JsonUtil.jsonToObject(Response, JDBatchSyncStockResponseObj.class);
            if (result.getSuccess()) {
                break;
            } else if (result.getCode().equals("10032")) {
                Thread.sleep(10000);//如果为成功，延迟10秒继续调用京东批量改价接口
            } else {
                log.error("调用京东批量更新价格接口返回异常,请求次数：" + num + ",返回结果：" + Response);
                break;
            }
        } while (num < 10);
        return result;
    }


    public JDBatchSyncPriceResponseObj callJingdongAPIToSYNCPrice(List<JDGoodsPriceItemObj> tmpjDGoodsPriceItemObjList) throws SQLException, ClassNotFoundException, InterruptedException, ParseException {
        int num = 0;//反复调用京东接口次数
        JDBatchSyncPriceResponseObj result = null;
        do {
            num++;
            JDBatchSyncPriceRequestObj jDBatchSyncPriceRequestObj = new JDBatchSyncPriceRequestObj();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());

            jDBatchSyncPriceRequestObj.getJd_param_json().setGoodsItemList(tmpjDGoodsPriceItemObjList);
            String token = common.getNewToken();
            String sign = common.GetRequestSign(JsonUtil.objectToString(jDBatchSyncPriceRequestObj.getJd_param_json()), currentTime, "1.0", token);

            jDBatchSyncPriceRequestObj.setSign(sign);

            String postData = "v=" + jDBatchSyncPriceRequestObj.getV()
                    + "&format=" + jDBatchSyncPriceRequestObj.getFormat()
                    + "&app_key=" + jDBatchSyncPriceRequestObj.getApp_key()
                    + "&app_secret=" + jDBatchSyncPriceRequestObj.getApp_secret()
                    //+ "&token=" + jDBatchSyncPriceRequestObj.getToken()
                    + "&token=" + token
                    + "&jd_param_json=" + JsonUtil.objectToString(jDBatchSyncPriceRequestObj.getJd_param_json())
                    + "&sign=" + jDBatchSyncPriceRequestObj.getSign()
                    + "&timestamp=" + currentTime;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<String>(postData, headers);

            String requestTime = dateFormat.format(new Date());
            long apiMillis = System.currentTimeMillis();
            // log.info("准备调用京东端批量更新价格接口：" + jddjProperty.getApiUrl() + "/price/batchUpdate" + ",数据数量：" + tmpjDGoodsPriceItemObjList.size() + ",请求次数：" + num + ",请求数据：" + postData + ",请求时间：" + requestTime);
            log.info("准备调用京东端批量更新价格接口：" + jddjProperty.getApiUrl() + "/price/batchUpdate" + ",数据数量：" + tmpjDGoodsPriceItemObjList.size() + ",请求次数：" + num + ",请求时间：" + requestTime);

            String Response = restTemplate.postForObject(jddjProperty.getApiUrl() + "/price/batchUpdate", entity, String.class);
            Response = common.getClearJsonString(Response);

            log.info("京东端批量更新价格接口返回数据：" + Response + ",耗时：" + (System.currentTimeMillis() - apiMillis));
            result = JsonUtil.jsonToObject(Response, JDBatchSyncPriceResponseObj.class);
            if (result.getSuccess()) {
                break;
            } else if (result.getCode() == "10032") {
                Thread.sleep(10000);//如果为成功，延迟5秒继续调用京东批量改价接口
            } else {
                log.error("调用京东批量更新价格接口返回异常,请求次数：" + num + ",返回结果：" + Response);
                break;
            }
        } while (num < 10);
        return result;
    }


    public JDChangePriceResponseObj callJingdongAPIToChangePrice(String stationNo, String skuId, BigDecimal price, BigDecimal market) throws UnsupportedEncodingException {
        JDChangePriceRequestPara obj = new JDChangePriceRequestPara();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        obj.getJd_param_json().setSkuId(skuId);
        obj.getJd_param_json().setStationNo(stationNo);
        obj.getJd_param_json().setPrice(price.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).toString());//分
        obj.getJd_param_json().setMarketPrice(market.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).toString());//分
        String token = common.getNewToken();
        String sign = common.GetRequestSign(JsonUtil.objectToString(obj.getJd_param_json()), currentTime, "1.0", token);
        obj.setSign(sign);
        String postData = "v=" + obj.getV()
                + "&format=" + obj.getFormat()
                + "&app_key=" + obj.getApp_key()
                + "&app_secret=" + obj.getApp_secret()
                // + "&token=" + obj.getToken()
                + "&token=" + token
                + "&jd_param_json=" + JsonUtil.objectToString(obj.getJd_param_json())
                // + "&jd_param_json=" +  java.net.URLEncoder.encode(JsonUtil.objectToString(obj.getJd_param_json()),"UTF-8")
                + "&sign=" + obj.getSign()
                + "&timestamp=" + currentTime;
        // + "&timestamp=" +  java.net.URLEncoder.encode(currentTime,"utf-8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(postData, headers);
        String Response = restTemplate.postForObject(jddjProperty.getApiUrl() + "/price/updateStationPrice", entity, String.class);
        // String Response = restTemplate.getForObject(jddjProperty.getApiUrl() + "/price/updateStationPrice?"+postData,String.class);//格式错误
        // String Response = common.httpClientGet(jddjProperty.getApiUrl() + "/price/updateStationPrice", postData);//调用京东接口
        Response = common.getClearJsonString(Response);

        JDChangePriceResponseObj result = JsonUtil.jsonToObject(Response, JDChangePriceResponseObj.class);

        return result;
    }


    public JDSingleStockUpdateResponseObj UpdateCurrentQty(String jdShop, String jdgood, int validStock) {
        JDSingleStockUpdateRequestPara obj = new JDSingleStockUpdateRequestPara();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        obj.getJd_param_json().setStationNo(jdShop);
        obj.getJd_param_json().setSkuId(jdgood);
        obj.getJd_param_json().setCurrentQty(String.valueOf(validStock));
        String token = common.getNewToken();
        String sign = common.GetRequestSign(JsonUtil.objectToString(obj.getJd_param_json()), currentTime, "1.0", token);
        obj.setSign(sign);
        String postData = "v=" + obj.getV()
                + "&format=" + obj.getFormat()
                + "&app_key=" + obj.getApp_key()
                + "&app_secret=" + obj.getApp_secret()
                //+ "&token=" + obj.getToken()
                + "&token=" + token
                + "&jd_param_json=" + JsonUtil.objectToString(obj.getJd_param_json())
                + "&sign=" + obj.getSign()
                + "&timestamp=" + currentTime;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>(postData, headers);
        String Response = restTemplate.postForObject(jddjProperty.getApiUrl() + "/update/currentQty", entity, String.class);
        Response = common.getClearJsonString(Response);

        JDSingleStockUpdateResponseObj result = JsonUtil.jsonToObject(Response, JDSingleStockUpdateResponseObj.class);
        return result;

    }


    /**
     * 获取京东到家商品数量
     *
     * @return
     */
    public int getJdProductCount() {
        JDGoodsInfoResponseObj result = getJdProductDetailList(1, 1);
        int total = 0;
        if (result.getSuccess()) {
            if (result.getData().getCode().equals("0")) {
                total = result.getData().getResult().getCount();
            }
        }

        return total;
    }


    /**
     * 获取京东到家商品列表
     * @param page
     * @param size
     * @return
     */
    public List<DtJDGoodsInfoResponseGoods> getJdProductList(int page, int size) {
        JDGoodsInfoResponseObj result = getJdProductDetailList(1, 1);
        List<DtJDGoodsInfoResponseGoods> list=new ArrayList<>();
        if(result.getSuccess()){
            if(result.getData().getCode().equals("0")){
                list=result.getData().getResult().getResult();
            }
        }

        return list;
    }


    public JDGoodsInfoResponseObj getJdProductDetailList(int page, int size) {
        JDGoodsInfoRequestObj obj = new JDGoodsInfoRequestObj();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(new Date());

        obj.getJd_param_json().setPageNo(page);
        obj.getJd_param_json().setPageSize(size);

        String token = common.getNewToken();
        String sign = common.GetRequestSign(JsonUtil.objectToString(obj.getJd_param_json()), currentTime, "1.0", token);
        obj.setSign(sign);
        String postData = "v=" + obj.getV()
                + "&format=" + obj.getFormat()
                + "&app_key=" + obj.getApp_key()
                + "&app_secret=" + obj.getApp_secret()
                //+ "&token=" + obj.getToken()
                + "&token=" + token
                + "&jd_param_json=" + JsonUtil.objectToString(obj.getJd_param_json())
                + "&sign=" + obj.getSign()
                + "&timestamp=" + currentTime;

        HttpHeaders heards = new HttpHeaders();
        heards.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>(postData, heards);
        String Response = restTemplate.postForObject(jddjProperty.getApiUrl() + "/pms/querySkuInfos", entity, String.class);
        Response= common.getClearJsonString(Response);
        JDGoodsInfoResponseObj result = JsonUtil.jsonToObject(Response, JDGoodsInfoResponseObj.class);
        return result;
    }


}
