package com.example.business.model;

import com.example.business.CommonJingdong.Common;
import com.example.utils.JsonUtil;
import com.example.utils.property.JddjProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/2/10.
 */
@Service
public class ThreadSendProduce  implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ThreadSendProduce.class);

    @Autowired
    Common common;

    @Autowired
    JddjProperty jddjProperty;

    @Autowired
    JDDJ_Repository jDDJ_Repository;

    private String orderId;

    private int type;

//    public ThreadSendProduce(String orderIdp,int typep){
//        orderId=orderIdp.trim();
//        type=typep;
//    }

    public ThreadSendProduce(){


    }


    public void init(String orderIdp,int typep){
        orderId=orderIdp.trim();
        type=typep;

    }
    public void run(){

        if (orderId.startsWith("2"))
        {
            //售后单
            try
            {
                //部分退货售后单号
               JDAfsQueryRequestParam obj = new JDAfsQueryRequestParam();

                obj.setAfsServiceOrder(orderId);

                String getData = common.GetRequestData(JsonUtil.objectToString(obj),"1.0");

                String url = jddjProperty.getApiUrl() + "/afs/getAfsService";

                String jdResponse = common.httpClientGet(url, getData);//调用京东售后订单查询接口

                String newStr = common.getClearJsonString(jdResponse);

                JDAfsResponseObj jdResult = JsonUtil.jsonToObject(newStr,JDAfsResponseObj.class);

                if (jdResult.getSuccess())
                {
                    if (jdResult.getData().getCode().equals("0"))
                    {
                        if (null!=jdResult.getData().getResult()  && jdResult.getData().getResult().getOrderId().compareTo(BigDecimal.ZERO)>0)
                        {
                            String sjShopCode = jdResult.getData().getResult().getStationNumOutSystem();
                            if (jdResult.getData().getResult().getAfsDetailList().size() > 0)
                            {
                                List<ProduceInfo> produceInfoList = new ArrayList<>();
                                for (JDAfsResponseAfsDetail detail : jdResult.getData().getResult().getAfsDetailList())
                                {
                                    ProduceInfo produceInfo = new ProduceInfo();
                                    produceInfo.setPlatformId(10002);
                                    produceInfo.setSjGoodsCode(detail.getSkuIdIsv());
                                    produceInfo.setSjShopCode(sjShopCode);
                                    produceInfo.setAmount(detail.getSkuCount());
                                    produceInfo.setType("return");
                                    produceInfoList.add(produceInfo);
                                }
                                RestTemplate restTemplate = new RestTemplate();
                                restTemplate.postForObject(jddjProperty.getCenterUrl()+"/stock/updateStockNumAndPreholdNum",
                                        produceInfoList, Object.class);
                               // httpPostWithJSON( jddjProperty.getCenterUrl()+"/stock/updateStockNumAndPreholdNum",JsonUtil.objectToString(produceInfoList));
                            }
                            else
                            {
                               log.error("售后单：" + orderId + "  " + "没有商品列表！");
                            }
                        }
                    }
                    else
                    {

                        log.error("售后单：" + orderId + "  " + "调用京东接口afs/getAfsService错误：" + jdResult.getData().getMsg());
                    }
                }

            }
            catch (Exception ex)
            {

              log.error("售后单：" + orderId + "  " + "处理中发生错误：" + ex.toString());
            }
        }
        else
        {
            //普通订单
            try
            {
                //普通单号（非售后单）京东到家请求参数(老的查询接口参数)
                OrderInfoRequestPara oldobj = new OrderInfoRequestPara();
                oldobj.getJd_param_json().setPageNo("1");
                oldobj.getJd_param_json().setPageSize("1");
                oldobj.getJd_param_json().setOrderId(orderId);
                String oldgetData = common.GetRequestData(JsonUtil.objectToString(oldobj.getJd_param_json()),"1.0");
                String url =jddjProperty.getApiUrl() + "/order/query";//普通订单查询接口
                String oldjdResponse = common.httpClientGet(url, oldgetData);
                String oldnewStr = common.getClearJsonString(oldjdResponse);
                JDOrderQueryResponseObj oldjdResult = JsonUtil.jsonToObject(oldnewStr,JDOrderQueryResponseObj.class);
                if (oldjdResult.getSuccess())
                {
                    if (oldjdResult.getData().getCode().equals("0"))
                    {
                        if (null!=oldjdResult.getData().getResult() && oldjdResult.getData().getResult().getResultList().size() > 0)
                        {
                            JDOrderQueryOrderMain order = (JDOrderQueryOrderMain) oldjdResult.getData().getResult().getResultList().stream().filter(p -> p.getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString().equals(orderId)).findFirst().orElse(null);
                            if (order.getOrderId().compareTo(BigDecimal.ZERO)>0)
                            {
                                String sjShopCode = order.getProduceStationNoIsv();
                                if (order.getOrderProductList().size() > 0)
                                {
                                    List<ProduceInfo> produceInfoList = new ArrayList<>();
                                    for (JDOrderQueryOrderProduct product : order.getOrderProductList())
                                    {
                                        ProduceInfo produceInfo = new ProduceInfo();
                                        produceInfo.setPlatformId(10002);
                                        produceInfo.setSjGoodsCode(product.getSkuIdIsv());
                                        produceInfo.setSjShopCode(sjShopCode);
                                        produceInfo.setAmount(product.getSkuCount()) ;
                                        if (type == 1)
                                        {
                                            produceInfo.setType("pos");

                                        }
                                        if (type == 2)
                                        {
                                            if (jDDJ_Repository.SercherPosOrder(orderId, 1) >= 1)
                                                produceInfo.setType("return");
                                            else
                                                produceInfo.setType("cancel");

                                        }
                                        produceInfoList.add(produceInfo);
                                    }
                                    RestTemplate restTemplate = new RestTemplate();
                                    restTemplate.postForObject(jddjProperty.getCenterUrl()+"/stock/updateStockNumAndPreholdNum",
                                            produceInfoList, Object.class);
                                   // httpPostWithJSON( jddjProperty.getCenterUrl()+"/stock/updateStockNumAndPreholdNum",JsonUtil.objectToString(produceInfoList));
                                }
                                else
                                {
                                   log.error("普通订单：" + orderId + "  " + "没有商品列表！");
                                }
                            }
                        }
                    }
                    else
                    {
                        log.error("普通订单：" + orderId + "  " + "调用京东接口orderInfo/query错误：" + oldjdResult.getData().getMsg());
                    }
                }
            }
            catch (Exception ex)
            {
                log.error("普通订单：" + orderId + "  " + "处理中发生错误：" + ex.toString());
            }
        }

    }






}
