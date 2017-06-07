package com.example.business.service;

import com.example.business.CommonJingdong.Common;
import com.example.business.model.*;
import com.example.domain.jddj.JdbcTest;
import com.example.domain.sjhub.PlatformShop;
import com.example.repository.jddj.TestRepository;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.repository.sjhub.TestttRepository;
import com.example.utils.JsonUtil;
import com.example.utils.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Created by wujianlong on 2017/3/14.
 */
@Service
public class OrderService {

    int orderDiscountMoney = 0;

    @Autowired
    Common common;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, String> hashOperations;

    String logMess = "";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");

    @Autowired
    TestttRepository testttRepository;

    @Autowired
    TestRepository testRepository;

    @Autowired
    ThreadSendProduce threadSendProduce;

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @Autowired
    protected PlatformShopRepository shopRepository;

    @Autowired
    JDDJ_Repository jDDJ_Repository;

    // @Transactional(transactionManager="sjHubTransactionManager")
    @Transactional(transactionManager = "jddjTransactionManager")
    public void Save() {
//        testttRepository.save(new TestTt("2","3334"));
//        testttRepository.save(new TestTt("444444444","4444444"));
//        testttRepository.save(new TestTt("3","3"));

        testRepository.save(new JdbcTest(1, "2", "1"));
        testRepository.save(new JdbcTest(2, "2", "2"));
        testRepository.save(new JdbcTest(3, "3", "3"));
        testRepository.save(new JdbcTest(4, "4", "4"));
    }


    public String orderSearch(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) {
        PosReponseOrderObj result = new PosReponseOrderObj();//返回的对象
        String logMessage = "";
        if (null != requestparam) {
            String requestTime = dateFormat.format(new Date());//请求的开始时间
            String orderId = requestparam.getOrderId().trim();//请求的订单号
            String requestData = JsonUtil.objectToString(requestparam); //序列化请求的数据
            String requestUrl = servletRequest.getRequestURI();//请求的URL
            String requestIp = servletRequest.getRemoteAddr();//发起请求的IP
            logMessage = "订单" + orderId + "查询请求日志：\r\n请求时间：" + requestTime + ";请求数据：" + requestData + ";请求的URL:" + requestUrl + ";发起请求的IP:" + requestIp;

            try {
                //调用京东到家普通订单（非售后单）查询接口；由于已取消的订单无法进行拆分明细，所以先调用普通订单查询接口，判断是否已取消，如果订单未取消就继续调用之后的订单金额拆分接口
                String orderQueryResult = common.OrderQuery(orderId);
                JDOrderQueryResponseObj queryResultobj = JsonUtil.jsonToObject(orderQueryResult, JDOrderQueryResponseObj.class);

                if (queryResultobj.getSuccess() && queryResultobj.getData().getCode().equals("0")) {
                    if (null != queryResultobj.getData().getResult() && queryResultobj.getData().getResult().getResultList().size() > 0) {
                        JDOrderQueryOrderMain order = (JDOrderQueryOrderMain) queryResultobj.getData().getResult().getResultList().stream().filter(p -> p.getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString().equals(orderId)).findFirst().orElse(null);
                        if (order.getOrderId().compareTo(BigDecimal.ZERO) > 0) {
                            if (order.getOrderStatus() == 20020)//判断是否为取消订单
                            {
                                //取消订单的处理方法，获取返回值
                                result = CancelOrderProcess(order);
                            } else//未取消订单调用拆分明细接口
                            {
                                //调用订单金额拆分明细接口
                                String orderInfoQueryResult = common.OrderInfoQuery(orderId);
                                JDOrderInfoQueryResponseObj jdResult = JsonUtil.jsonToObject(orderInfoQueryResult, JDOrderInfoQueryResponseObj.class);

                                //未取消订单的处理方法，获取返回值
                                result = NoCancelOrderProcess(jdResult);
                            }

                        }
                    } else {
                        //交互成功，对象不存在
                        result.setStatus(3);
                    }
                }

            } catch (Exception ex) {
                result.setStatus(9999);
                log.error("订单查询失败，订单号：" + orderId + ";错误原因：" + ex.toString());
            }
        }

        String ResponseResult = JsonUtil.objectToString(result);
        logMessage += ";\r\n最终返回结果：" + ResponseResult;
        log.info(logMessage);

        return ResponseResult;
    }


    public String OrderPos(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) {
        JsExcuteStatus result = new JsExcuteStatus();//返回的对象
        String logMessage = "";
        if (null != requestparam) {
            String requestTime = dateFormat.format(new Date());//请求的开始时间
            String orderId = requestparam.getOrderId().trim();//请求的订单号
            String requestData = JsonUtil.objectToString(requestparam); //序列化请求的数据
            String requestUrl = servletRequest.getRequestURI();//请求的URL
            String requestIp = servletRequest.getRemoteAddr();//发起请求的IP
            logMessage = "订单" + orderId + "过机请求日志：\r\n请求时间：" + requestTime + ";请求数据：" + requestData + ";请求的URL:" + requestUrl + ";发起请求的IP:" + requestIp;

            result = dealOrderPos(orderId, requestparam, servletRequest);

        } else {
            result.setStatus(10);//没有请求参数
        }

        String ResponseResult = JsonUtil.objectToString(result);
        logMessage += ";\r\n最终返回结果：" + ResponseResult;
        log.info(logMessage);
        return ResponseResult;
    }




    public String returnSercher(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        PosOrderReturnReponseObj result = new PosOrderReturnReponseObj();
        try {
            if (requestparam != null) {
                if (requestparam.getOrderId().startsWith("2")) {
                    //退货售后单调用京东售后单查询接口
                    String newStr = common.getAfsService(requestparam.getOrderId());
                    JDAfsResponseObj jdResult = JsonUtil.jsonToObject(newStr, JDAfsResponseObj.class);
                    if (jdResult.getSuccess()) {
                        if (jdResult.getData().getCode().equals("0")) {
                            if (jdResult.getData().getResult() != null && jdResult.getData().getResult().getOrderId().compareTo(BigDecimal.ZERO) > 0) {
                                common.AddAfsOrder(requestparam.getOrderId(),jdResult.getData().getResult());//将售后单信息保存至redis

                                result.setStatus(0);
                                result.getData().setOrderId(jdResult.getData().getResult().getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                                result.getData().setOrderStatus(jdResult.getData().getResult().getAfsServiceState());
                                BigDecimal goodsAmount = BigDecimal.ZERO;
                                BigDecimal discountAmout = BigDecimal.ZERO;
//                                afsToDb.init(jdResult.getData().getResult());
//                                Thread task1 = new Thread(afsToDb);
//                                task1.start();

                                String stationNo = jdResult.getData().getResult().getStationId().trim();
                                PlatformShop platformShop = shopRepository.findByPlatformIdAndPlatformShopCodeAndStatus("10002", stationNo, 1);
                                result.getData().setStoreId(platformShop != null ? platformShop.getSjShopCode() : "");

                                for (JDAfsResponseAfsDetail detail : jdResult.getData().getResult().getAfsDetailList()) {
                                    PosOrderReturnReponseGoods goods = new PosOrderReturnReponseGoods();
                                    goods.setGoodsId(detail.getSkuIdIsv());
                                    goods.setGoodsName(detail.getWareName());
                                    goods.setSalePrice(new BigDecimal(String.valueOf(detail.getPayPrice())).divide(new BigDecimal("100")));
                                    goods.setGoodsCount(detail.getSkuCount());

                                    for (JDAfsResponseAfsDiscount discount : detail.getDiscountLst()) {
                                        //优惠类型(1:优惠码;3:优惠劵;4:满减;5:满折;6:首单优惠)
                                        //小优惠类型(优惠码(1:满减;2:立减;3:满折;);优惠券(1:满减;2:立减;3:免运费劵;4:运费优惠N元;))

                                        String erpPromotionId = "";

                                        //1 满减/满折优惠 2 首单优惠 3 优惠券/码优惠
                                        //满减中ERP存在的促销活动 分摊金额
                                        if (discount.getDiscountType() == 1) {
                                            String promotionCode = jDDJ_Repository.QueryOrderSkuPromotionCode(String.valueOf(jdResult.getData().getResult().getOrderId()), String.valueOf(detail.getWareId()));
                                            if (!org.springframework.util.StringUtils.isEmpty(promotionCode)) {
                                                erpPromotionId = common.GetPromotionId(promotionCode);
                                            }
                                        }
                                        if (!org.springframework.util.StringUtils.isEmpty(erpPromotionId)) {
                                            goods.setERPDiscount(goods.getERPDiscount().add(new BigDecimal(String.valueOf(discount.getDiscountMoney())).divide(new BigDecimal("100"))));
                                            goods.setPromotionId(erpPromotionId);
                                        } else {
                                            goods.setDiscount(goods.getDiscount().add(new BigDecimal(String.valueOf(discount.getDiscountMoney())).divide(new BigDecimal("100"))));
                                        }
                                    }
                                    goodsAmount = goodsAmount.add(goods.getSalePrice().multiply(new BigDecimal(String.valueOf(goods.getGoodsCount()))));
                                    discountAmout = discountAmout.add(goods.getERPDiscount().add(goods.getDiscount()));
                                    result.getData().getGoodsList().add(goods);
                                }
                                result.getData().setGoodsAmount(goodsAmount);
                                result.getData().setOrderAmount(goodsAmount.subtract(discountAmout));
                            }
                        }
                    }
                } else {
                    String orderInfoQueryResult = common.OrderInfoQuery(requestparam.getOrderId());
                    JDOrderInfoQueryResponseObj jdResult = JsonUtil.jsonToObject(orderInfoQueryResult, JDOrderInfoQueryResponseObj.class);

                    if (jdResult.getSuccess()) {
                        if (jdResult.getData().getCode().equals("0")) {
                            if (jdResult.getData().getResult() != null && jdResult.getData().getResult().getOrderMain() != null && jdResult.getData().getResult().getOrderMain().getOrderId().compareTo(BigDecimal.ZERO) > 0) {
                                //未取消订单的处理方法
                                NoCancelOrderProcess(result, jdResult);
                            }
                        } else if (jdResult.getData().getCode().equals("1010") || jdResult.getData().getCode().equals("9999") || jdResult.getData().getCode().equals("-1"))//-1:1个月之前的订单无法通过接口查询只能查询本地数据库
                        {
                            String newskuIdIsv = "";
                            result.setStatus(0);//设置为0强制使退货前查询成功
                            JDOrderInfoQueryResponseOrderMain orderMain = jDDJ_Repository.GetLocalOrderMain(new BigDecimal(requestparam.getOrderId()));
                            result.getData().setOrderId(orderMain.getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                            result.getData().setOrderStatus(orderMain.getOrderStatus());


                            result.getData().setStoreId(orderMain.getProduceStationNoIsv());
                            orderDiscountMoney = orderMain.getOrderDiscountMoney();//初始优惠金额

                            List<JDOrderInfoQueryOrderDiscount> jDOrderInfoQueryOrderDiscountList = jDDJ_Repository.GetLocalOrderDiscountList(new BigDecimal(requestparam.getOrderId()));

                            if (jDOrderInfoQueryOrderDiscountList != null && jDOrderInfoQueryOrderDiscountList.size() > 0) {

                                for (JDOrderInfoQueryOrderDiscount obj : jDOrderInfoQueryOrderDiscountList) {

                                    if (obj.getDiscountType() == 3) {

                                        if (obj.getDiscountDetailType() == 3 || obj.getDiscountDetailType() == 4) {
                                            orderDiscountMoney = orderDiscountMoney - obj.getDiscountPrice();
                                        }
                                    }
                                }
                            }
                            jDOrderInfoQueryOrderDiscountList = null;

                            Map<String, PosOrderReturnReponseGoods> goodsList = new HashMap<>();
                            Map<String, String> tempIdDictnew = new HashMap<>();

                            List<JDOrderQueryOrderProduct> jDOrderQueryOrderProductList = jDDJ_Repository.GetLocalorderProductListt(new BigDecimal(requestparam.getOrderId()));
                            NoCancelOrderProductLocal(jDOrderQueryOrderProductList, tempIdDictnew, goodsList);
                            jDOrderQueryOrderProductList = null;

                            List<JDOrderInfoQueryResponseOassBussinessSku> jDOrderInfoQueryResponseOassBussinessSkuList = jDDJ_Repository.GetLocaloassBussinessSkus(new BigDecimal(requestparam.getOrderId()));
                            NoCancelOrderBussinessLocal(jDOrderInfoQueryResponseOassBussinessSkuList, tempIdDictnew, goodsList);
                            jDOrderInfoQueryResponseOassBussinessSkuList = null;

                            result.getData().setGoodsList(goodsList.values().stream().collect(Collectors.toList()));
                            result.getData().setOrderAmount(new BigDecimal(String.valueOf((orderMain.getOrderTotalMoney() - orderDiscountMoney))).divide(new BigDecimal("100")));
                            result.getData().setGoodsAmount(new BigDecimal(String.valueOf(orderMain.getOrderTotalMoney())).divide(new BigDecimal("100")));

                            //获取订单过机信息，主要退货用
                            result.getData().setPos(common.GetOrderPosInfo(orderMain.getOrderId()));
                        }
                    }
                }


            }
        } catch (Exception exception) {
            result.setStatus(9999);
        }
        String ResponseResult = JsonUtil.objectToString(result);
        return ResponseResult;
    }





    public String returnConfirm(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException, SQLException {
        Date dealTime = new Date();
        JsExcuteStatus result = new JsExcuteStatus();
        if (requestparam != null) {
            //'2'开头的订单为售后单，进行售后单退货操作
            if (requestparam.getOrderId().startsWith("2")) {
                //调用售后单确认退货接口
                String newStr = common.AfsServiceConfirm(requestparam.getOrderId(), requestparam.getOperator().getOperatorName(), requestparam.getPos().getStoreId());
                OrderReturnResponseObj orderReturnResponseObj = JsonUtil.jsonToObject(newStr, OrderReturnResponseObj.class);

                if (orderReturnResponseObj.getSuccess()) {
                    if (orderReturnResponseObj.getData().getCode().equals("0") || orderReturnResponseObj.getData().getCode().equals("9")) {
                        result.setStatus(0);
                    }
                    if (orderReturnResponseObj.getData().getCode().equals("-1")) {
                        //已确认退货再次点击退货
                        result.setStatus(0);
                    }
                }
                if (result.getStatus() == 0) {
                    AfsProcess afsProcess = new AfsProcess(requestparam, dealTime, servletRequest.getRemoteAddr());
                    //保存退货单信息和退货流水
                    if(!common.SaveAfsOrderAndAfsProcess(requestparam.getOrderId(),afsProcess))
                    {
                        result.setStatus(1);
                    }

                }
            } else {
                if (jDDJ_Repository.QueryIsOrderException(requestparam.getOrderId(), 2)) {
                    //异常订单，跳过京东接口强制可以退货
                    result.setStatus(0);
                } else {
                    //调用普通订单退货接口
                    String newStr = common.OrderReturn(requestparam.getOrderId(), requestparam.getPos().getPosTime());
                    OrderReturnResponseObj jdResult = JsonUtil.jsonToObject(newStr, OrderReturnResponseObj.class);

                    if (jdResult.getSuccess()) {
                        if (jdResult.getData().getCode().equals("0")) {
                            result.setStatus(0);
                            //退货成功后，要强制刷新库存
                            //每天定时强制刷新库存
                            //货到付款 未过机订单，直接减库存，取消后，不加现货库存

                        } else if (jdResult.getData().getCode().equals("-1") && jdResult.getData().getMsg().contains("没有收到锁定的订单记录") || (jdResult.getData().getCode().equals("-1") && jdResult.getData().getMsg().contains("查询订单出错或订单不存在"))) {
                            //临界点 订单过机和订单取消，也强制可以退货 或者 一个月前无法查询到的整单退货单也可以退货
                            result.setStatus(0);
                        }
                    }
                }
                if (result.getStatus() == 0) {
                    //只有通知过机成功的才需要记录流水
                    OrderProcess orderProcess = new OrderProcess(requestparam, 2, new Date(), servletRequest.getRemoteAddr());
                    Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
                    if (!common.SavePosProcess(conn, null, orderProcess, requestparam.getOrderId())) {
                        //保存过机流水失败，不准过机
                        result.setStatus(1);
                    }
                    if (conn != null)
                        conn.close();

                }
            }
            if (result.getStatus() == 0) {
                //传递订单商品信息给中台接口
                threadSendProduce.init(requestparam.getOrderId(), 2);
                Thread task3 = new Thread(threadSendProduce);
                task3.start();
            }

        }
        return JsonUtil.objectToString(result);
    }



    public void NoCancelOrderProductLocal(List<JDOrderQueryOrderProduct> productList, Map<String, String> tempIdDict, Map<String, PosOrderReturnReponseGoods> goodsList) {
        String newskuId;
        String newskuIdIsv;
        for (JDOrderQueryOrderProduct product : productList) {
            if (product.getPromotionType() == 6) {
                continue;
            }
            if (product.getPromotionType() != 1 && product.getSkuCostPrice() != null) {
                newskuId = String.valueOf(product.getSkuId()) + "_" + String.valueOf(product.getSkuCostPrice());
                newskuIdIsv = String.valueOf(product.getSkuIdIsv()) + "_" + String.valueOf(product.getSkuCostPrice());
            } else {
                newskuId = String.valueOf(product.getSkuId());
                newskuIdIsv = String.valueOf(product.getSkuIdIsv());
            }
            tempIdDict.put(newskuId, newskuIdIsv);

            PosOrderReturnReponseGoods goods = new PosOrderReturnReponseGoods();
            goods.setGoodsId(product.getSkuIdIsv());
            goods.setGoodsName(product.getSkuName());
            goods.setSalePrice(new BigDecimal(String.valueOf(product.getSkuJdPrice())).divide(new BigDecimal("100")));
            goods.setGoodsCount(product.getSkuCount());

            goodsList.put(newskuIdIsv, goods);

        }

    }


    public void NoCancelOrderBussinessLocal(List<JDOrderInfoQueryResponseOassBussinessSku> oassBussinessSkus, Map<String, String> tempIdDict, Map<String, PosOrderReturnReponseGoods> goodsList) {
        String newskuId;
        for (JDOrderInfoQueryResponseOassBussinessSku sku : oassBussinessSkus) {
            if (sku.getPromotionPrice() < sku.getPdjPrice()) {
                newskuId = String.valueOf(sku.getSkuId()) + "_" + String.valueOf(sku.getPromotionPrice());
            } else {
                newskuId = String.valueOf(sku.getSkuId());
            }

            if (tempIdDict.containsKey(newskuId)) {
                String goodsId = tempIdDict.get(newskuId);

                List<JDOrderInfoQueryResponseOrderBussiDiscountMoney> jDOrderInfoQueryResponseOrderBussiDiscountMoneyList = jDDJ_Repository.GetLocaloassBussinessDiscount(sku.getOrderId(), sku.getSkuId());

                if (null == jDOrderInfoQueryResponseOrderBussiDiscountMoneyList || jDOrderInfoQueryResponseOrderBussiDiscountMoneyList.size() <= 0)
                    return;
                for (JDOrderInfoQueryResponseOrderBussiDiscountMoney discount : jDOrderInfoQueryResponseOrderBussiDiscountMoneyList) {
                    String erpPromotionId = "";

                    //目前ERP活动只有满减
                    if (discount.getPromotionType() == 4) {

                        erpPromotionId = common.GetPromotionId(discount.getPromotionCode());
                    }

                    if (!(null == erpPromotionId || erpPromotionId.isEmpty())) {
                        goodsList.get(goodsId).setERPDiscount(new BigDecimal(String.valueOf(discount.getSkuDiscountMoney())).divide(new BigDecimal("100")));
                        goodsList.get(goodsId).setPromotionId(erpPromotionId);
                    } else {
                        goodsList.get(goodsId).setDiscount(new BigDecimal(String.valueOf(discount.getSkuDiscountMoney())).divide(new BigDecimal("100")));
                    }

                }
                jDOrderInfoQueryResponseOrderBussiDiscountMoneyList = null;
            }
        }

    }


    public JsExcuteStatus dealOrderPos(String orderId, PosRequestOrderProcessParam requestParam, HttpServletRequest servletRequest) {
        JsExcuteStatus result = new JsExcuteStatus();
        try {
            if (StringUtils.isEmpty(orderId) || !common.isNum(orderId)) {
                result.setStatus(11);//请求订单号不能为空或者不为整数
                return result;
            }

            //测试，暂时注释掉
            if (null != common.GetOrderPosInfo(new BigDecimal(orderId))) {
                result.setStatus(200);//订单之前过机已经成功过
                return result;
            }

            //过机前，确认当前订单信息与之前查询的订单信息是否一致
            if (!common.CheckorderInfo(orderId)) {
                result.setStatus(300);//订单信息不一致
                return result;
            }

            //调用京东到家拣货完成接口
            String OrderDeliveryResult = common.OrderDelivery(orderId, requestParam.getOperator().getOperatorName());
            OrderJDZBDeliveryResponseObj jdResult = JsonUtil.jsonToObject(OrderDeliveryResult, OrderJDZBDeliveryResponseObj.class);

            if (jdResult.getSuccess()) {
                //大促期间 京东到家系统每隔两分钟，会自动拣货完成，对于预计配送时间过了的订单
                if (jdResult.getData().getCode().equals(Constant.JDDJ_PICK_UP_SUCCESS) || jdResult.getData().getCode().equals(Constant.JDDJ_PICK_UP_PRODUCT_FINISH)) {
                    //过机成功的话，把订单信息导入数据库
                    OrderProcess orderProcess = new OrderProcess(requestParam, 1, new Date(), servletRequest.getRemoteAddr());

                    //判断是否成功保存订单和流水号至本地数据库
                    if (common.SaveOrderAndPosProcess(orderId, orderProcess)) {
                        result.setStatus(0);
                    }

                } else if (jdResult.getData().getCode().equals(Constant.JDDJ_PICK_UP_ABNORMAL)) {
                    //订单号：100001017294902调用调度平台修改承运商信息失败没有找到要处理的订单[100001017294902]
                    //检查订单是否取消，如果没有取消，请联系
                    result.setStatus(201);
                }
            }

            if (result.getStatus() == 0) {
                //传递订单商品信息给中台接口
                threadSendProduce.init(orderId, 1);
                executor.execute(threadSendProduce);
                log.info("订单：" + orderId + "开启记录传递订单信息给中台的线程时间：" + dateFormat.format(new Date()));
            }

        } catch (Exception ex) {
            result.setStatus(9999);//接口异常
            log.error("订单过机失败，订单号：" + orderId + ";错误原因：" + ex.toString());
        }

        return result;

    }



    public void NoCancelOrderProcess(PosOrderReturnReponseObj result, JDOrderInfoQueryResponseObj jdResult) {

        JDOrderInfoQueryResponseResult jdresultdataresult = jdResult.getData().getResult();
        if (jdresultdataresult != null && jdresultdataresult.getOrderMain() != null && jdresultdataresult.getOrderMain().getOrderId().compareTo(BigDecimal.ZERO) > 0) {
            result.setStatus(0);

            JDOrderInfoQueryResponseOrderMain orderMain = jdresultdataresult.getOrderMain();
            List<JDOrderInfoQueryResponseOassBussinessSku> oassBussinessSkus = jdresultdataresult.getOassBussinessSkus();

            PosOrderReturnReponseData ResultData = result.getData();
            ResultData.setOrderId(orderMain.getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
            ResultData.setOrderStatus(orderMain.getOrderStatus());


            //京东方门店号
            ResultData.setStoreId(orderMain.getProduceStationNoIsv());

            orderDiscountMoney = orderMain.getOrderDiscountMoney();//初始优惠金额

            //未取消订单优惠详情处理方法
            NoCancelOrderDiscount(orderMain);


            Map<String, PosOrderReturnReponseGoods> goodsList = new HashMap();
            Map<String, String> tempIdDict = new HashMap();

            //超出限购商品，超出部分按原价购买，会产生两条同一个sku但价格不同的商品列表(由于oassBussinessSkus无法和orderProductList的同一个商品相对应，暂时以优惠价格为标准对应，如果遇到优惠价格相同的同一个商品会出现问题)  2016-09-27
            String newskuId = "";
            String newskuIdIsv = "";

            //未取消订单商品详情处理方法
            NoCancelOrderProduct1(orderMain, tempIdDict, goodsList);

            //未取消订单金额拆分明细详情处理方法
            NoCancelOrderBussiness1(oassBussinessSkus, tempIdDict, goodsList);

            ResultData.setGoodsList(goodsList.values().stream().collect(Collectors.toList()));

            ResultData.setOrderAmount(new BigDecimal(String.valueOf((orderMain.getOrderTotalMoney() - orderDiscountMoney))).divide(new BigDecimal("100")));
            ResultData.setGoodsAmount(new BigDecimal(String.valueOf(orderMain.getOrderTotalMoney())).divide(new BigDecimal("100")));

            //获取订单过机信息，主要退货用
            ResultData.setPos(common.GetOrderPosInfo(orderMain.getOrderId()));

        }
    }






    public PosReponseOrderObj CancelOrderProcess(JDOrderQueryOrderMain order) {
        PosReponseOrderObj result = new PosReponseOrderObj();
        result.setStatus(0);
        result.getData().setOrderId(order.getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
        //32000:等待出库 能过机
        result.getData().setOrderStatus(order.getOrderStatus());

        //根据配送类型来决定过机时调用哪个拣货完成的接口
        if (order.getDeliveryType() == 2) {
            common.AddSellerDeliveryOrder(order.getOrderId(), order.getDeliveryType());
        }

        //京东方门店号
        result.getData().setStoreId(order.getProduceStationNoIsv());

        orderDiscountMoney = order.getOrderDiscountMoney();

        Map<String, TempERPPromotion> erpPromotion = new HashMap<>();
        List<String> existSkuIdList = new ArrayList<>();
        Boolean isErpPromotionError = false;

        //取消订单的优惠详情处理方法
        isErpPromotionError = CancelOrderDiscount(order, erpPromotion, existSkuIdList);

        Map<String, DtPosReponseGoods> goodsList = new HashMap<>();
        if (isErpPromotionError) {
            //记录异常
            log.error("isErpPromotionError，订单号：" + order.getOrderId());
        }
        if (erpPromotion.size() > 0 && isErpPromotionError == false) {
            Map<String, String> tempIdDict = new HashMap<>();

            //取消订单的商品详情处理方法
            CancelOrderProduct(order, tempIdDict, erpPromotion, goodsList);

            //分摊金额详情处理方法
            CancelOrderMoney(order, erpPromotion, tempIdDict, goodsList);

            result.getData().setGoodsList(goodsList.values().stream().collect(Collectors.toList()));

        } else {
            //取消订单的没有促销活动的商品详情处理方法
            CancelOrderNoproProduct(order, goodsList, result);

        }
        result.getData().setOrderAmount((new BigDecimal(String.valueOf(order.getOrderTotalMoney())).subtract(new BigDecimal(String.valueOf(orderDiscountMoney)))).divide(new BigDecimal("100")));
        result.getData().setGoodsAmount(new BigDecimal(String.valueOf(order.getOrderTotalMoney())).divide(new BigDecimal("100")));

        //获取订单过机信息，主要退货用
        result.getData().setPos(common.GetOrderPosInfo(order.getOrderId()));
        //没有过机订单，需要保存订单信息
        if (null == (result.getData().getPos()) || null == (result.getData().getPos().getPosNo()) || (result.getData().getPos().getPosNo()).isEmpty()) {
            common.AddPosOrder(order.getOrderId(), order);
        }
        // logMessage += "；数据处理完成时间：" + dateFormat.format(new Date());
        return result;
    }


    public boolean CancelOrderDiscount(JDOrderQueryOrderMain order, Map<String, TempERPPromotion> erpPromotion, List<String> existSkuIdList) {
        boolean isErpPromotionError = false;
        for (JDOrderQueryOrderDiscount discount : order.getOrderDiscountList()) {
            //优惠券
            if (discount.getDiscountType() == 3) {
                //免运费券、运费优惠N元
                if (discount.getDiscountDetailType() == 3 || discount.getDiscountDetailType() == 4) {
                    orderDiscountMoney = orderDiscountMoney - discount.getDiscountPrice();
                }

            }

            //满减
            if (discount.getDiscountType() == 4) {
                TempERPPromotion tempErp = new TempERPPromotion();
                tempErp.setJDDJPromotionId(discount.getDiscountCode().trim());

                //如果存在，代表这个是ERP当中的满减，否则则是京东到家专享（采购走费用单）
                tempErp.setERPPromotionId(common.GetPromotionId(tempErp.getJDDJPromotionId()));

                //discount.discountPrice
                String[] list = discount.getSkuIds().split(",");

                tempErp.setDiscount(discount.getDiscountPrice());

                for (String skuId : list) {
                    if (!(null == skuId || skuId.isEmpty())) {
                        tempErp.getSkuIdList().add(skuId);
                        if (existSkuIdList.contains(skuId)) {
                            isErpPromotionError = true;
                        } else {
                            existSkuIdList.add(skuId);
                        }
                    }
                }
                erpPromotion.put(tempErp.getJDDJPromotionId(), tempErp);

            }
        }

        return isErpPromotionError;
    }

    public void CancelOrderProduct(JDOrderQueryOrderMain order, Map<String, String> tempIdDict, Map<String, TempERPPromotion> erpPromotion, Map<String, DtPosReponseGoods> goodsList) {
        for (JDOrderQueryOrderProduct product : order.getOrderProductList()) {
            if (tempIdDict.containsKey(String.valueOf(product.getSkuId())))//同个sku多条记录处理
            {
                continue;
            }
            DtPosReponseGoods goods = new DtPosReponseGoods();
            goods.setGoodsId(product.getSkuIdIsv());
            goods.setGoodsName(product.getSkuName());
            goods.setSalePrice(new BigDecimal(String.valueOf(product.getSkuJdPrice())).divide(new BigDecimal("100"), 2));
            goods.setGoodsCount(product.getSkuCount());

            tempIdDict.put(String.valueOf(product.getSkuId()), product.getSkuIdIsv());
            goodsList.put(goods.getGoodsId(), goods);

            //同一件商品只能参与一个ERP活动
            TempERPPromotion tempERP = erpPromotion.values().stream().filter(p -> p.getSkuIdList().contains(String.valueOf(product.getSkuId()))).findFirst().orElse(null);

            if (tempERP != null && !(null == tempERP.getJDDJPromotionId() || tempERP.getJDDJPromotionId().isEmpty())) {
                erpPromotion.get(tempERP.getJDDJPromotionId()).setAmount(erpPromotion.get(tempERP.getJDDJPromotionId()).getAmount() + product.getSkuJdPrice() * product.getSkuCount());
            }
        }
    }

    public void CancelOrderMoney(JDOrderQueryOrderMain order, Map<String, TempERPPromotion> erpPromotion, Map<String, String> tempIdDict, Map<String, DtPosReponseGoods> goodsList) {
        for (TempERPPromotion tempERP : erpPromotion.values()) {
            if (tempERP.getSkuIdList().size() > 1) {
                BigDecimal tempTotalDiscount = BigDecimal.ZERO;//已经优惠总金额 精度到0.0001元

                List<String> tempSkuIdList = GetSkuIdOrderByAmountDesc(tempERP.getSkuIdList(), order.getOrderProductList());

                for (int i = 0; i < tempSkuIdList.size() - 1; i++) {

                    String goodsId = tempIdDict.get(tempSkuIdList.get(i));

                    goodsList.get(goodsId).setPromotionId(tempERP.getERPPromotionId());

                    //goodsList[goodsId].Discount = Math.Round(goodsList[goodsId].SalePrice * 100 * goodsList[goodsId].GoodsCount/tempERP.Amount,2) * tempERP.Discount / 100;
                    //goodsList[goodsId].Discount = Common.SJRule.GetDecimal2Digit(goodsList[goodsId].SalePrice * 100 * goodsList[goodsId].GoodsCount, tempERP.Amount) * tempERP.Discount / 100;

                    //精度只保留分  ROUND_DOWN--接近零的舍入模式。(只要值大于零就类似于不进行四舍五入)
                    goodsList.get(goodsId).setDiscount(((goodsList.get(goodsId).getSalePrice().multiply(new BigDecimal("100")).multiply(new BigDecimal(goodsList.get(goodsId).getGoodsCount())).divide(new BigDecimal(tempERP.getAmount()))).setScale(2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(tempERP.getDiscount()))).setScale(0, BigDecimal.ROUND_DOWN).divide(new BigDecimal("100")));
                    tempTotalDiscount = tempTotalDiscount.add(goodsList.get(goodsId).getDiscount());

                }
                String tempGoodsId = tempIdDict.get(tempSkuIdList.get(tempSkuIdList.size() - 1));
                goodsList.get(tempGoodsId).setPromotionId(tempERP.getERPPromotionId());
                goodsList.get(tempGoodsId).setDiscount(new BigDecimal(String.valueOf(tempERP.getDiscount())).divide(new BigDecimal("100")).subtract(tempTotalDiscount));
            } else {
                String goodsId = tempIdDict.get(tempERP.getSkuIdList().get(0));
                goodsList.get(goodsId).setPromotionId(tempERP.getERPPromotionId());
                goodsList.get(goodsId).setDiscount(new BigDecimal(String.valueOf(tempERP.getDiscount())).divide(new BigDecimal("100")));
            }
        }
    }


    public List<String> GetSkuIdOrderByAmountDesc(List<String> list, List<JDOrderQueryOrderProduct> dict) {
        List<String> result = new ArrayList<>();

        Comparator<JDOrderQueryOrderProduct> c = (p, o) -> Integer.valueOf(p.getSkuCount() * p.getSkuJdPrice()).compareTo(Integer.valueOf(o.getSkuCount() * o.getSkuJdPrice()));
        dict.sort(c);
        for (JDOrderQueryOrderProduct obj : dict) {
            if (list.contains(String.valueOf(obj.getSkuId()))) {
                result.add(String.valueOf(obj.getSkuId()));
            }
        }
        return result;
    }


    public void CancelOrderNoproProduct(JDOrderQueryOrderMain order, Map<String, DtPosReponseGoods> goodsList, PosReponseOrderObj result) {

        for (JDOrderQueryOrderProduct product : order.getOrderProductList()) {
            DtPosReponseGoods goods = new DtPosReponseGoods();
            goods.setGoodsId(product.getSkuIdIsv());
            goods.setGoodsName(product.getSkuName());
            goods.setSalePrice(new BigDecimal(String.valueOf(product.getSkuJdPrice())).divide(new BigDecimal("100")));
            goods.setGoodsCount(product.getSkuCount());

            result.getData().getGoodsList().add(goods);
        }
    }


    public PosReponseOrderObj NoCancelOrderProcess(JDOrderInfoQueryResponseObj jdResult) {
        PosReponseOrderObj result = new PosReponseOrderObj();
        if (jdResult.getSuccess()) {
            if (jdResult.getData().getCode().equals("0")) {
                JDOrderInfoQueryResponseResult jdresultdataresult = jdResult.getData().getResult();
                if (jdresultdataresult != null && jdresultdataresult.getOrderMain() != null && jdresultdataresult.getOrderMain().getOrderId().compareTo(BigDecimal.ZERO) > 0) {
                    result.setStatus(0);

                    JDOrderInfoQueryResponseOrderMain orderMain = jdresultdataresult.getOrderMain();
                    List<JDOrderInfoQueryResponseOassBussinessSku> oassBussinessSkus = jdresultdataresult.getOassBussinessSkus();

                    DtPosReponseOrder ResultData = result.getData();
                    ResultData.setOrderId(orderMain.getOrderId().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
                    ResultData.setOrderStatus(orderMain.getOrderStatus());

                    if (orderMain.getDeliveryType() == 2) {
                        common.AddSellerDeliveryOrder(orderMain.getOrderId(), orderMain.getOrderStatus());
                    }

                    //京东方门店号
                    ResultData.setStoreId(orderMain.getProduceStationNoIsv());

                    orderDiscountMoney = orderMain.getOrderDiscountMoney();//初始优惠金额

                    //未取消订单优惠详情处理方法
                    NoCancelOrderDiscount(orderMain);


                    Map<String, DtPosReponseGoods> goodsList = new HashMap();
                    Map<String, String> tempIdDict = new HashMap();

                    //超出限购商品，超出部分按原价购买，会产生两条同一个sku但价格不同的商品列表(由于oassBussinessSkus无法和orderProductList的同一个商品相对应，暂时以优惠价格为标准对应，如果遇到优惠价格相同的同一个商品会出现问题)  2016-09-27
                    String newskuId = "";
                    String newskuIdIsv = "";

                    //未取消订单商品详情处理方法
                    NoCancelOrderProduct(orderMain, tempIdDict, goodsList);

                    //未取消订单金额拆分明细详情处理方法
                    NoCancelOrderBussiness(oassBussinessSkus, tempIdDict, goodsList);

                    ResultData.setGoodsList(goodsList.values().stream().collect(Collectors.toList()));

                    ResultData.setOrderAmount(new BigDecimal(String.valueOf((orderMain.getOrderTotalMoney() - orderDiscountMoney))).divide(new BigDecimal("100")));
                    ResultData.setGoodsAmount(new BigDecimal(String.valueOf(orderMain.getOrderTotalMoney())).divide(new BigDecimal("100")));

                    //获取订单过机信息，主要退货用
                    ResultData.setPos(common.GetOrderPosInfo(orderMain.getOrderId()));
                    // ResultData.setPos(null);//测试
                    //没有过机订单，需要保存订单信息
                    if (null == ResultData.getPos() || null == ResultData.getPos().getPosNo() || ResultData.getPos().getPosNo().isEmpty()) {
                        common.AddDetailPosOrder(orderMain.getOrderId(), jdresultdataresult);
                }
                    //logMessage += "；数据处理完成时间：" + dateFormat.format(new Date());

                }
            } else if (jdResult.getData().getCode().equals("1010")) {
                result.setStatus(203);
                //{"code":"0","msg":"操作成功, UUID[272ba4cae93e4145bf773d188f2d7566]","data":{"result":null,"detail":null,"code":"1010","msg":"取消订单不能获取拆分明细","success":false},"success":true}
            }
        }
        return result;
    }

    public void NoCancelOrderDiscount(JDOrderInfoQueryResponseOrderMain orderMain) {

        for (JDOrderInfoQueryOrderDiscount discount : orderMain.getOrderDiscountList()) {
            //优惠券
            if (discount.getDiscountType() == 3) {
                //免运费券、运费优惠N元
                if (discount.getDiscountDetailType() == 3 || discount.getDiscountDetailType() == 4) {
                    orderDiscountMoney = orderDiscountMoney - discount.getDiscountPrice();
                }

            }
        }
    }


    public void NoCancelOrderProduct1(JDOrderInfoQueryResponseOrderMain orderMain, Map<String, String> tempIdDict, Map<String, PosOrderReturnReponseGoods> goodsList) {
        String newskuId;
        String newskuIdIsv;
        for (JDOrderQueryOrderProduct product : orderMain.getOrderProductList()) {
            if (product.getPromotionType() == 6) {
                continue;
            }
            if (product.getPromotionType() != 1 && product.getSkuCostPrice() != null) {
                newskuId = String.valueOf(product.getSkuId()) + "_" + String.valueOf(product.getSkuCostPrice());
                newskuIdIsv = String.valueOf(product.getSkuIdIsv()) + "_" + String.valueOf(product.getSkuCostPrice());
            } else {
                newskuId = String.valueOf(product.getSkuId());
                newskuIdIsv = String.valueOf(product.getSkuIdIsv());
            }
            tempIdDict.put(newskuId, newskuIdIsv);

            PosOrderReturnReponseGoods goods = new PosOrderReturnReponseGoods();
            goods.setGoodsId(product.getSkuIdIsv());
            goods.setGoodsName(product.getSkuName());
            goods.setSalePrice(new BigDecimal(String.valueOf(product.getSkuJdPrice())).divide(new BigDecimal("100")));
            goods.setGoodsCount(product.getSkuCount());

            goodsList.put(newskuIdIsv, goods);

        }

    }


    public void NoCancelOrderProduct(JDOrderInfoQueryResponseOrderMain orderMain, Map<String, String> tempIdDict, Map<String, DtPosReponseGoods> goodsList) {
        String newskuId;
        String newskuIdIsv;
        for (JDOrderQueryOrderProduct product : orderMain.getOrderProductList()) {
            if (product.getPromotionType() == 6) {
                continue;
            }
            if (product.getPromotionType() != 1 && product.getSkuCostPrice() != null) {
                newskuId = String.valueOf(product.getSkuId()) + "_" + String.valueOf(product.getSkuCostPrice());
                newskuIdIsv = String.valueOf(product.getSkuIdIsv()) + "_" + String.valueOf(product.getSkuCostPrice());
            } else {
                newskuId = String.valueOf(product.getSkuId());
                newskuIdIsv = String.valueOf(product.getSkuIdIsv());
            }
            tempIdDict.put(newskuId, newskuIdIsv);

            DtPosReponseGoods goods = new DtPosReponseGoods();
            goods.setGoodsId(product.getSkuIdIsv());
            goods.setGoodsName(product.getSkuName());
            goods.setSalePrice(new BigDecimal(String.valueOf(product.getSkuJdPrice())).divide(new BigDecimal("100")));
            goods.setGoodsCount(product.getSkuCount());

            goodsList.put(newskuIdIsv, goods);

        }

    }

    public void NoCancelOrderBussiness1(List<JDOrderInfoQueryResponseOassBussinessSku> oassBussinessSkus, Map<String, String> tempIdDict, Map<String, PosOrderReturnReponseGoods> goodsList) {
        String newskuId;
        for (JDOrderInfoQueryResponseOassBussinessSku sku : oassBussinessSkus) {
            if (sku.getPromotionPrice() < sku.getPdjPrice()) {
                newskuId = String.valueOf(sku.getSkuId()) + "_" + String.valueOf(sku.getPromotionPrice());
            } else {
                newskuId = String.valueOf(sku.getSkuId());
            }

            if (tempIdDict.containsKey(newskuId)) {
                String goodsId = tempIdDict.get(newskuId);

                for (JDOrderInfoQueryResponseOrderBussiDiscountMoney discount : sku.getDiscountlist()) {
                    String erpPromotionId = "";

                    //目前ERP活动只有满减
                    if (discount.getPromotionType() == 4) {

                        erpPromotionId = common.GetPromotionId(discount.getPromotionCode());
                    }

                    if (!(null == erpPromotionId || erpPromotionId.isEmpty())) {
                        goodsList.get(goodsId).setERPDiscount(new BigDecimal(String.valueOf(discount.getSkuDiscountMoney())).divide(new BigDecimal("100")));
                        goodsList.get(goodsId).setPromotionId(erpPromotionId);
                    } else {
                        goodsList.get(goodsId).setDiscount(new BigDecimal(String.valueOf(discount.getSkuDiscountMoney())).divide(new BigDecimal("100")));
                    }

                }

            }
        }

    }



    public void NoCancelOrderBussiness(List<JDOrderInfoQueryResponseOassBussinessSku> oassBussinessSkus, Map<String, String> tempIdDict, Map<String, DtPosReponseGoods> goodsList) {
        String newskuId;
        for (JDOrderInfoQueryResponseOassBussinessSku sku : oassBussinessSkus) {
            if (sku.getPromotionPrice() < sku.getPdjPrice()) {
                newskuId = String.valueOf(sku.getSkuId()) + "_" + String.valueOf(sku.getPromotionPrice());
            } else {
                newskuId = String.valueOf(sku.getSkuId());
            }

            if (tempIdDict.containsKey(newskuId)) {
                String goodsId = tempIdDict.get(newskuId);

                for (JDOrderInfoQueryResponseOrderBussiDiscountMoney discount : sku.getDiscountlist()) {
                    String erpPromotionId = "";

                    //目前ERP活动只有满减
                    if (discount.getPromotionType() == 4) {

                        erpPromotionId = common.GetPromotionId(discount.getPromotionCode());
                    }

                    if (!(null == erpPromotionId || erpPromotionId.isEmpty())) {
                        goodsList.get(goodsId).setERPDiscount(new BigDecimal(String.valueOf(discount.getSkuDiscountMoney())).divide(new BigDecimal("100")));
                        goodsList.get(goodsId).setPromotionId(erpPromotionId);
                    } else {
                        goodsList.get(goodsId).setDiscount(new BigDecimal(String.valueOf(discount.getSkuDiscountMoney())).divide(new BigDecimal("100")));
                    }

                }

            }
        }

    }

}
