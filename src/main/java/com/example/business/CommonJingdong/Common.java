package com.example.business.CommonJingdong;

import com.example.business.model.*;
import com.example.domain.jddj.ApiToken;
import com.example.domain.sjhub.PlatformShop;
import com.example.repository.jddj.ApiTokenRepository;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.utils.JsonUtil;
import com.example.utils.property.JddjProperty;
import com.example.utils.property.RedisProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;


import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wujianlong on 2017/3/6.
 */
@Service
public class Common {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    SimpleDateFormat dateFormatnew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger log = LoggerFactory.getLogger(Common.class);

    @Autowired
    private JddjProperty jddjProperty;


    @Resource(name = "redisTemplate")
    protected ValueOperations<String, List<ERPPromotionObj>> valueOperations;

    private static final String Promotion_SQL = "select * from V_POS_ERP_PROMOTION_MAIN";

    private static final String Pos_SQL = "select * from JDDJ_ORDER_POS where order_id=?";

    @Autowired
    private JdbcTemplate jingDongDaoJiaJdbcTemplate;

    @Autowired
    private RedisProperty redisProperty;

    @Autowired
    private ApiTokenRepository apiTokenRepository;

    @Autowired
    private JdbcTemplate dwhJdbcTemplate;

    @Autowired
    private JDDJ_Repository jDDJ_Repository;

    @Autowired
    protected PlatformShopRepository shopRepository;


    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, String> hashOperations;

    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, String> hashOperationsNorOrder;

    @Resource(name = "redisTemplate")
    protected HashOperations<String, String, String> hashOperationsDetailOrder;


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
    SimpleDateFormat simpleDateFormatnew = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //调用普通订单查询接口
    public String OrderQuery(String orderId) throws UnsupportedEncodingException {
        String logMes = "";
        OrderInfoRequestPara oldobj = new OrderInfoRequestPara();
        oldobj.getJd_param_json().setPageNo("1");
        oldobj.getJd_param_json().setPageSize("1");
        oldobj.getJd_param_json().setOrderId(orderId);
        String getData = GetRequestData(JsonUtil.objectToString(oldobj.getJd_param_json()), "1.0");
        logMes += "调用京东普通订单查询接口--" + "订单号J" + orderId + ":\r\n";
        String url = jddjProperty.getApiUrl() + "/order/query";//普通订单查询接口
        logMes += "，请求时间：" + dateFormat.format(new Date());
        String Response = httpClientGet(url, getData);//调用京东普通订单查询接口
        logMes += "，京东接口返回时间：" + dateFormat.format(new Date());
        String result = getClearJsonString(Response);
        logMes += "，请求URL：" + url + "\r\n请求参数：" + getData + "\r\n返回参数：" + result;
        log.info(logMes);

        return result;
    }

    /**
     * 调用京东到家查询流水号接口
     *
     * @param batchNo 流水号
     * @return
     * @throws UnsupportedEncodingException
     */
    public String queryBatch(String batchNo) throws UnsupportedEncodingException {
        String logMes = "";
        JDBatchModifyStockResultRequestPara obj = new JDBatchModifyStockResultRequestPara();
        obj.getJd_param_json().setBatchNo(batchNo);
        String getData = GetRequestData(JsonUtil.objectToString(obj.getJd_param_json()), "1.0");

        logMes += "调用京东到家查询流水号接口--" + "流水号" + batchNo + ":\r\n";
        String url = jddjProperty.getApiUrl() + "/stock/queryBatchUpdateResult";//京东到家查询流水号接口
        logMes += "，请求时间：" + dateFormat.format(new Date());
        String jdResponse = httpClientGet(url, getData);
      /*  HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>(getData, headers);
        RestTemplate restTemplate = new RestTemplate();
        String jdResponse = restTemplate.postForObject(url, entity, String.class);*/

        logMes += "，京东接口返回时间：" + dateFormat.format(new Date());
        String result = getClearJsonString(jdResponse);
        logMes += "，请求URL：" + url + "\r\n请求参数：" + getData + "\r\n返回参数：" + result;
        log.info(logMes);

        return result;
    }


    //调用订单金额拆分明细接口
    public String OrderInfoQuery(String orderId) throws UnsupportedEncodingException {
        String logMes = "";
        //普通单号（非售后单）京东到家请求参数
        JDOrderInfoQueryParamJson obj = new JDOrderInfoQueryParamJson();
        obj.setOrderId(orderId);
        //拼接get请求字符串
        String getData = GetRequestData(JsonUtil.objectToString(obj), "1.0");
        logMes += "调用京东订单金额拆分接口--" + "订单号J" + orderId + ":\r\n";
        String url = jddjProperty.getApiUrl() + "/orderInfo/query";//订单金额拆分明细接口
        logMes += "，请求时间：" + dateFormat.format(new Date());
        String jdResponse = httpClientGet(url, getData);
        logMes += "，京东接口返回时间：" + dateFormat.format(new Date());
        String result = getClearJsonString(jdResponse);
        logMes += "，请求URL：" + url + "\r\n请求参数：" + getData + "\r\n返回参数：" + result;
        log.info(logMes);

        return result;
    }


    //调用售后单查询接口
    public String getAfsService(String orderId) throws UnsupportedEncodingException {
        JDAfsQueryRequestParam obj = new JDAfsQueryRequestParam();
        obj.setAfsServiceOrder(orderId);
        String getData = GetRequestData(JsonUtil.objectToString(obj), "1.0");
        String url = jddjProperty.getApiUrl() + "/afs/getAfsService";
        String jdResponse = httpClientGet(url, getData);
        String newStr = getClearJsonString(jdResponse);

        return newStr;
    }

    public String AfsServiceConfirm(String orderId, String operatorName, String storeId) throws UnsupportedEncodingException {
        JDAfsConfirmReceiptRequestParam obj = new JDAfsConfirmReceiptRequestParam();

        obj.setAfsServiceOrder(orderId);
        obj.setPin(operatorName);
        PlatformShop platformShop = shopRepository.findBySjShopCodeAndPlatformIdAndStatus(storeId, "10002", 1);
        if (null != platformShop)
            obj.setStationNo(platformShop.getPlatformShopCode());
        //拼接get请求字符串
        String getData = GetRequestData(JsonUtil.objectToString(obj), "1.0");
        String url = jddjProperty.getApiUrl() + "/afs/confirmReceipt";
        String jdResponse = httpClientGet(url, getData);
        String newStr = getClearJsonString(jdResponse);

        return newStr;
    }

    //调用普通订单退货接口
    public String OrderReturn(String oderId, String posTime) throws UnsupportedEncodingException {
        DtOrderReturnRequest obj = new DtOrderReturnRequest();
        obj.setOrderId(oderId);
        obj.setOperateTime(posTime);

        String getData = GetRequestData(JsonUtil.objectToString(obj), "1.0");
        String url = jddjProperty.getApiUrl() + "/order/confirmReceiveGoods";
        String jdResponse = httpClientGet(url, getData);
        String newStr = getClearJsonString(jdResponse);

        return newStr;
    }

    public String GetRequestData(String jd_param_json, String v) throws UnsupportedEncodingException {
        String now = dateFormatnew.format(new Date());
        JDSysRequestParam obj = new JDSysRequestParam();
        String token=getNewToken();
        obj.setSign(GetRequestSign(jd_param_json, now, v,token));
        String getData = "v=" + v
                + "&format=" + obj.getFormat()
                + "&app_key=" + obj.getApp_key()
                + "&app_secret=" + obj.getApp_secret()
                //+ "&token=" + obj.getToken()
                + "&token=" + token
                //  + "&jd_param_json=" + jd_param_json
                + "&jd_param_json=" + java.net.URLEncoder.encode(jd_param_json, "UTF-8")
                + "&sign=" + obj.getSign()
                //  + "&timestamp=" + now;
                + "&timestamp=" + java.net.URLEncoder.encode(now, "UTF-8");
        return getData;
    }


    public String GetRequestSign(String jd_param_json, String now, String v,String token) {
        JDSysRequestParam obj = new JDSysRequestParam();
        String str = obj.getApp_secret()
                + "app_key" + obj.getApp_key()
                + "format" + obj.getFormat()
                + "jd_param_json" + jd_param_json
                + "timestamp" + now
               // + "token" + obj.getToken()
                + "token" + token
                + "v" + v
                + obj.getApp_secret();
        String sign = BytesConvertToHexString(EncryptionStrBytes(str, "MD5")).toUpperCase();
        return sign;
    }

    private static byte[] EncryptionStrBytes(String str, String algorithm) {
        // 加密之后所得字节数组
        byte[] bytes = null;
        try {
            // 获取MD5算法实例 得到一个md5的消息摘要
            MessageDigest md = MessageDigest.getInstance(algorithm);
            //添加要进行计算摘要的信息
            md.update(str.getBytes());
            //得到该摘要
            bytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("加密算法: " + algorithm + " 不存在: ");
        }
        return null == bytes ? null : bytes;
    }

    /**
     * 把字节数组转化成字符串返回
     *
     * @param bytes
     * @return
     */
    private static String BytesConvertToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte aByte : bytes) {
            String s = Integer.toHexString(0xff & aByte);
            if (s.length() == 1) {
                sb.append("0" + s);
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }


    public String getClearJsonString(String Response) {
        Response = Response.replace("\\", "").replace("\"{", "{").replace("}\"", "}");
        char[] temp = Response.toCharArray();
        int n = temp.length;

        //把字符串值中的"替换为中文“”，商品名称
        for (int i = 1; i < n; i++) {
            if (temp[i - 1] == '"' && temp[i] == ':' && temp[i + 1] == '"') {
                for (int j = i + 2; j < n; j++) {
                    if (temp[j] == '"') {
                        if (temp[j + 1] != ',' && temp[j + 1] != '}') {
                            temp[j] = '”';
                        } else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
                            break;
                        }
                    }
                }
            }
        }
        return new String(temp);

    }


    /// <summary>
    /// 截取指定字节数的字符串，超出部分用...
    /// </summary>
    /// <param name="origStr">原始字符串</param>
    /// <param name="endIndex">提取前endIdex个字节</param>
    /// <returns></returns>
    public static String GetSubString(String origStr, int endIndex) throws UnsupportedEncodingException {
        if (null == origStr || origStr.length() == 0 || endIndex < 0)
            return "";
        int bytesCount = origStr.getBytes("GB2312").length;
        if (bytesCount > endIndex) {
            int readyLength = 0;
            int byteLength;
            for (int i = 0; i < origStr.length(); i++) {
                byteLength = String.valueOf(origStr.charAt(i)).getBytes("GB2312").length;
                readyLength += byteLength;
                if (readyLength == endIndex) {
                    origStr = origStr.substring(0, i + 1) + "...";
                    break;
                } else if (readyLength > endIndex) {
                    origStr = origStr.substring(0, i) + "...";
                    break;
                }
            }
        }
        return origStr;
    }


    /**
     * @Description:使用HttpClient发送get请求
     * @author:liuyc
     * @time:2016年5月17日 下午3:28:56
     */
    public String httpClientGet(String urlParam, String sbParams) {
        StringBuffer resultBuffer = null;
        HttpClient client = new DefaultHttpClient();
        BufferedReader br = null;
        // 构建请求参数

        if (sbParams != null && sbParams.length() > 0) {
            urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length());
        }

        HttpGet httpGet = new HttpGet(urlParam);
        try {
            HttpResponse response = client.execute(httpGet);
            // 读取服务器响应数据
            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String temp;
            resultBuffer = new StringBuffer();
            while ((temp = br.readLine()) != null) {
                resultBuffer.append(temp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                    throw new RuntimeException(e);
                }
            }
        }
        return resultBuffer.toString();
    }


    public String GetPromotionId(String jddjPromotionId) {
        String result = "";
//        String redisKey = "Promotion";
//        ERPPromotionObj eRPPromotionListObj;
//        List<ERPPromotionObj> eRPPromotionList = valueOperations.get(redisKey);
//        if (CollectionUtils.isNotEmpty(eRPPromotionList)) {
//            eRPPromotionListObj = eRPPromotionList.stream().filter(p -> p.getJDDJPromotionId().equals(jddjPromotionId)).findFirst().orElse(null);
//            if (null != eRPPromotionListObj) {
//                result = eRPPromotionListObj.getPromotionId();
//            }
//        } else {
        // List<ERPPromotionObj> eRPPromotionObjDbList = GetAllPromtion();
//            valueOperations.set(redisKey, eRPPromotionObjDbList);
//            eRPPromotionListObj = eRPPromotionObjDbList.stream().filter(p -> p.getJDDJPromotionId().equals(jddjPromotionId)).findFirst().orElse(null);
//            if (null != eRPPromotionListObj) {
//                result = eRPPromotionListObj.getPromotionId();
//            }
//        }
        ERPPromotionObj eRPPromotionListObj;
        List<ERPPromotionObj> eRPPromotionObjDbList = GetAllPromtion();
        if (CollectionUtils.isNotEmpty(eRPPromotionObjDbList)) {
            eRPPromotionListObj = eRPPromotionObjDbList.stream().filter(p -> p.getJDDJPromotionId().equals(jddjPromotionId)).findFirst().orElse(null);
            if (null != eRPPromotionListObj) {
                result = eRPPromotionListObj.getPromotionId();
            }
        }
        return result;

    }


    public List<ERPPromotionObj> GetAllPromtion() {
        return jingDongDaoJiaJdbcTemplate.query(Promotion_SQL, getRowMapper());
    }

    private RowMapper<ERPPromotionObj> getRowMapper() {
        return (resultSet, i) -> {

            ERPPromotionObj eRPPromotionObj = new ERPPromotionObj();
            eRPPromotionObj.setPromotionId(resultSet.getString("PROMOTION_ID"));
            eRPPromotionObj.setJDDJPromotionId(resultSet.getString("JDDJ_PROMOTION_ID"));
            eRPPromotionObj.setPromotionType(resultSet.getInt("PROMOTION_TYPE"));
            eRPPromotionObj.setStartDate((Date) resultSet.getDate("START_DATE"));
            eRPPromotionObj.setEndDate((Date) resultSet.getDate("END_DATE"));

            return eRPPromotionObj;
        };
    }


    /// <summary>
    /// 获取订单过机信息
    /// </summary>
    /// <param name="orderId"></param>
    /// <returns></returns>
    public PosParam GetOrderPosInfo(BigDecimal orderId) {
        String resultstr;
        PosParam result = new PosParam();
        List<PosParam> resultList = null;
        String message = "";
        String redisKey = "OrderPosParam:";
        try {
            Jedis jedis = new Jedis(redisProperty.getHost());
            jedis.select(Integer.valueOf(redisProperty.getDatabase()));
            resultstr = jedis.get(redisKey + String.valueOf(orderId));
            //  jedis.setex(redisKey+String.valueOf(orderId),3600,JsonUtil.objectToString(param));//缓存1小时
            //  resultstr = hashOperationsPos.get(redisKey, String.valueOf(orderId));
            if (null == resultstr || resultstr.isEmpty()) {
                resultList = GetPosParam(String.valueOf(orderId));
                if (CollectionUtils.isNotEmpty(resultList)) {
                    result = resultList.get(0);
                    resultstr = JsonUtil.objectToString(result);
                    jedis.setex(redisKey + String.valueOf(orderId), 3600, resultstr);//缓存1小时
                    //hashOperationsPos.put(redisKey, String.valueOf(orderId), resultstr);
                }
            } else {
                result = JsonUtil.jsonToObject(resultstr, PosParam.class);
            }
        } catch (Exception ex) {
            message = "GetOrderPosInfo()，发生异常出错，错误信息：" + ex.toString();
        }
        log.info(message);

        return result;
    }

    public List<PosParam> GetPosParam(String orderId) {
        return dwhJdbcTemplate.query(Pos_SQL, new Object[]{orderId}, getPosMapper());
    }

    private RowMapper<PosParam> getPosMapper() {
        return (resultSet, i) -> {
            PosParam posParam = new PosParam();
            posParam.setPosNo(resultSet.getString("POS_NO"));
            posParam.setPosStream(resultSet.getString("POS_STREAM"));
            posParam.setPosTime(resultSet.getString("POS_TIME"));
            posParam.setStoreId(resultSet.getString("STORE_ID"));

            return posParam;
        };


    }


    public void AddSellerDeliveryOrder(BigDecimal orderId, int deliveryType) {
        String redisKey = "DeliveryOrder";
        hashOperations.put(redisKey, String.valueOf(orderId), String.valueOf(deliveryType));

    }


    public void AddPosOrder(BigDecimal orderId, JDOrderQueryOrderMain order) {
        String redisKey = "Normal_Order";
        hashOperationsNorOrder.put(redisKey, String.valueOf(orderId), JsonUtil.objectToString(order));
    }


    public void AddDetailPosOrder(BigDecimal orderId, JDOrderInfoQueryResponseResult order) {
        String redisKey = "Detail_Order";
        hashOperationsDetailOrder.put(redisKey, String.valueOf(orderId), JsonUtil.objectToString(order));
    }


    public void AddAfsOrder(String orderId, JDAfsResponseResult afsOrder) {
        String redisKey = "Afs_Order";
        hashOperationsDetailOrder.put(redisKey, orderId, JsonUtil.objectToString(afsOrder));
    }

    public boolean isNum(String str) {
        return str.matches("[0-9]+");//纯数字
        // str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");//可以有小数点
    }


    public boolean CheckorderInfo(String orderId) throws UnsupportedEncodingException {
        boolean result = false;
        String detailInfo = hashOperationsDetailOrder.get("Detail_Order", String.valueOf(orderId));
        if (StringUtils.isNotEmpty(detailInfo)) {
            JDOrderInfoQueryResponseResult order = JsonUtil.jsonToObject(detailInfo, JDOrderInfoQueryResponseResult.class);
            //调用订单金额拆分明细接口
            String orderInfoQueryResult = OrderInfoQuery(orderId);
            JDOrderInfoQueryResponseObj jdResult = JsonUtil.jsonToObject(orderInfoQueryResult, JDOrderInfoQueryResponseObj.class);
            if (jdResult.getSuccess()) {
                if (jdResult.getData().getCode().equals("0")) {
                    JDOrderInfoQueryResponseResult jdresultdataresult = jdResult.getData().getResult();
                    if (jdresultdataresult != null && jdresultdataresult.getOrderMain() != null && jdresultdataresult.getOrderMain().getOrderId().compareTo(BigDecimal.ZERO) > 0) {
                        JDOrderInfoQueryResponseOrderMain orderMain = jdresultdataresult.getOrderMain();
                        if (orderMain.getOrderStatus() == order.getOrderMain().getOrderStatus() && orderMain.getOrderTotalMoney() == order.getOrderMain().getOrderTotalMoney()) {
                            result = true;
                        }

                    }
                }
            }

        }

        return result;
    }


    //调用订单拣货完成接口
    public String OrderDelivery(String orderId, String operatorName) throws UnsupportedEncodingException {
        String result = "";
        //请求京东
        OrderJDZBDeliveryRequestPara obj = new OrderJDZBDeliveryRequestPara();
        obj.getJd_param_json().setOrderId(orderId);
        obj.getJd_param_json().setOperator(operatorName);

        String getData = GetRequestData(JsonUtil.objectToString(obj.getJd_param_json()), "1.1");
        String url = "";

        if (GetSellerDeliveryOrder(orderId)) {
            url = jddjProperty.getApiUrl() + "/bm/open/api/order/OrderSerllerDelivery";
            DelSellerDeliveryOrder(orderId);
        } else {
            url = jddjProperty.getApiUrl() + "/bm/open/api/order/OrderJDZBDelivery";
        }

        String oldjdResponse = httpClientGet(url, getData);//调用订单拣货完成接口
        result = getClearJsonString(oldjdResponse);

        return result;
    }

    public boolean GetSellerDeliveryOrder(String orderId) {
        boolean re = false;
        String redisKey = "DeliveryOrder";
        String result = hashOperations.get(redisKey, orderId);
        if (null == result || result.isEmpty() || !result.equals("2")) {
            re = false;
        } else {
            re = true;
        }
        return re;
    }

    public void DelSellerDeliveryOrder(String orderId) {
        String redisKey = "DeliveryOrder";
        hashOperations.delete(redisKey, orderId);

    }

    public boolean SaveAfsOrderAndAfsProcess(String orderId, AfsProcess afsProcess) {
        boolean result = false;
        String afsInfo = hashOperationsNorOrder.get("Afs_Order", String.valueOf(orderId));//获取售后单信息

        if (StringUtils.isNotEmpty(afsInfo)) {
            try {
                //保存取消订单信息
                result = AfsToDb(JsonUtil.jsonToObject(afsInfo, JDAfsResponseResult.class), afsProcess);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            if (result == true) {
                hashOperationsNorOrder.delete("Afs_Order", orderId);
            }

        }

        return result;
    }


    public boolean SaveOrderAndPosProcess(String orderId, OrderProcess orderProcess) {
        boolean result = false;
        String normalInfo = hashOperationsNorOrder.get("Normal_Order", String.valueOf(orderId));//取消订单的订单信息(暂时不考虑保存，因为取消订单无法过机)
        String detailInfo = hashOperationsDetailOrder.get("Detail_Order", String.valueOf(orderId));//未取消订单的订单信息

        if (StringUtils.isEmpty(normalInfo) && StringUtils.isEmpty(detailInfo)) {
            return false;
        }
        if (StringUtils.isNotEmpty(normalInfo) && StringUtils.isEmpty(detailInfo)) {
            try {
                //保存取消订单信息和过机流水的事件
                result = OrderInfoToDb(JsonUtil.jsonToObject(normalInfo, JDOrderQueryOrderMain.class), orderProcess);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            if (result == true) {
                hashOperationsNorOrder.delete("Normal_Order", String.valueOf(orderId));
            }
        }
        if (StringUtils.isEmpty(normalInfo) && StringUtils.isNotEmpty(detailInfo)) {
            try {
                //保存未取消订单信息和过机流水的事件
                result = DetailOrderInfoToDb(JsonUtil.jsonToObject(detailInfo, JDOrderInfoQueryResponseResult.class), orderProcess);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            if (result == true) {
                hashOperationsDetailOrder.delete("Detail_Order", String.valueOf(orderId));
            }
        }

        return result;
    }


    public boolean AfsToDb(JDAfsResponseResult afsInfo, AfsProcess afsProcess) throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        Savepoint savepoint1 = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
            conn.setAutoCommit(false);
            savepoint1 = conn.setSavepoint("Savepoint1");

            //过机成功的订单，不管订单状态，商品和促销信息都不会变更
            int excute = jDDJ_Repository.AddAfsMain(afsInfo, conn, cstmt);
            if (excute > 0) {
                //删除
                int temp_excute = 0;
                for (JDAfsResponseAfsDetail detail : afsInfo.getAfsDetailList()) {
                    temp_excute = jDDJ_Repository.AddAfsDetail(afsInfo.getAfsServiceOrder(), detail, conn, cstmt);

                    if (temp_excute <= 0) {
                        log.error("新增退货单明细失败 AddAfsDetail，订单Id：" + afsInfo.getAfsServiceOrder() + "，wareId：" + detail.getWareId());
                        conn.rollback(savepoint1);
                        return false;
                    } else {
                        for (JDAfsResponseAfsDiscount discount : detail.getDiscountLst()) {
                            temp_excute = jDDJ_Repository.AddAfsDiscount(afsInfo.getAfsServiceOrder(), new BigDecimal(String.valueOf(detail.getWareId())), discount, conn, cstmt);

                            if (temp_excute <= 0) {
                                log.error("新增优惠明细失败 AddAfsDiscount，退货单Id：" + afsInfo.getAfsServiceOrder() + "，wareId：" + detail.getWareId());
                                conn.rollback(savepoint1);
                                return false;
                            }
                        }
                    }
                }
            } else {
                log.error("新增售后单失败 AddAfsMain，订单Id：" + afsInfo.getAfsServiceOrder());
                conn.rollback(savepoint1);
                return false;
            }

            if (!SaveAfsProcess(conn, cstmt, afsProcess, String.valueOf(afsInfo.getAfsServiceOrder()))) {
                log.error("新增退货流水失败，订单Id：" + afsInfo.getAfsServiceOrder().toString());
                conn.rollback(savepoint1);
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            log.error(ex.toString());
            conn.rollback(savepoint1);
            return false;
        } finally {
            if (cstmt != null) try {
                cstmt.close();
            } catch (Exception e) {
            }
            if (conn != null) try {
                conn.close();
            } catch (Exception e) {
            }
        }

    }


    public boolean DetailOrderInfoToDb(JDOrderInfoQueryResponseResult jDOrderInfoQueryResponseResult, OrderProcess orderProcess) throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        Savepoint savepoint1 = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
            conn.setAutoCommit(false);
            savepoint1 = conn.setSavepoint("Savepoint1");
            //过机成功的订单，不管订单状态，商品和促销信息都不会变更

            //保存订单主表
            int excute = jDDJ_Repository.AddOrderInfoMain(jDOrderInfoQueryResponseResult.getOrderMain(), conn, cstmt);

            if (excute > 0) {
                //删除
                int temp_excute = 0;
                //由于订单买家备注的字节数超出数据库128字节数无法存储订单信息，所以进行字节限制110个
                jDOrderInfoQueryResponseResult.getOrderMain().getOrderExtend().setOrderBuyerRemark(GetSubString(jDOrderInfoQueryResponseResult.getOrderMain().getOrderExtend().getOrderBuyerRemark(), 110));
                //保存订单额外表
                temp_excute = jDDJ_Repository.AddOrderExtend(jDOrderInfoQueryResponseResult.getOrderMain().getOrderExtend(), conn, cstmt);
                if (temp_excute <= 0) {
                    log.error("新增完结订单失败 AddOrderExtend，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId());
                    conn.rollback(savepoint1);
                    return false;
                }

                //保存订单商品列表
                boolean productsave = jDDJ_Repository.AddOrderProductList(jDOrderInfoQueryResponseResult.getOrderMain().getOrderProductList(), conn, cstmt);
                if (productsave == false) {
                    log.error("新增完结订单失败 AddOrderProductList，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId());
                    conn.rollback(savepoint1);
                    return false;
                }

                //保存优惠列表
                boolean discountsave = jDDJ_Repository.AddOrderInfoDiscountList(jDOrderInfoQueryResponseResult.getOrderMain().getOrderDiscountList(), conn, cstmt);
                if (discountsave == false) {
                    log.error("新增完结订单失败 AddOrderInfoDiscountList，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId());
                    conn.rollback(savepoint1);
                    return false;
                }

                //新增分摊相关
                for (JDOrderInfoQueryResponseOassBussinessSku sku : jDOrderInfoQueryResponseResult.getOassBussinessSkus()) {
                    temp_excute = jDDJ_Repository.AddOassBussiSku(sku, conn, cstmt);
                    if (temp_excute > 0) {
                        if (sku.getDiscountlist().size() > 0) {
                            boolean bussiDiscountsave = jDDJ_Repository.AddOassBussiDiscountList(new BigDecimal(String.valueOf(sku.getOrderId())), new BigDecimal(String.valueOf(sku.getSkuId())), sku.getDiscountlist(), conn, cstmt);
                            if (bussiDiscountsave == false) {
                                log.error("新增完结订单失败 AddOassBussiDiscountList，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId());
                                conn.rollback(savepoint1);
                                return false;
                            }
                        }
                    } else {
                        log.error("新增完结订单失败 AddOassBussiSku，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId());
                        conn.rollback(savepoint1);
                        return false;
                    }
                }

            } else {
                log.error("新增完结订单失败 AddOrderInfoMain，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId().toString());
                conn.rollback(savepoint1);
                return false;
            }

            //保存过机流水
            if (!SavePosProcess(conn, cstmt, orderProcess, jDOrderInfoQueryResponseResult.getOrderMain().getOrderId().toString())) {
                log.error("新增过机流水失败，订单Id：" + jDOrderInfoQueryResponseResult.getOrderMain().getOrderId().toString());
                conn.rollback(savepoint1);
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            log.error(ex.toString());
            conn.rollback(savepoint1);
            return false;
        } finally {
            if (cstmt != null) try {
                cstmt.close();
            } catch (Exception e) {
            }
            if (conn != null) try {
                conn.close();
            } catch (Exception e) {
            }
        }

    }


    public boolean SavePosProcess(Connection conn, CallableStatement cstmt, OrderProcess request, String orderIdParam) {
        String logmess = "SavePosProcess(); 订单号：" + orderIdParam + "，类型：" + request.getProcessType() + "，开始时间 ：" + simpleDateFormat.format(new Date());
        try {
            if (request.getProcessType() == 1 || request.getProcessType() == 2) {
                if (null == request.getRequest().getPos().getPosTime() || request.getRequest().getPos().getPosTime().isEmpty()) {
                    log.error("SavePosProcess(); 过机和退货操作时，过机时间不能为空，Pos请求数据：" + JsonUtil.objectToString(request));
                    return false;
                }
            }

            Date posTime = (null == request.getRequest().getPos().getPosTime() || request.getRequest().getPos().getPosTime().isEmpty()) ? new Date() : simpleDateFormatnew.parse(request.getRequest().getPos().getPosTime());

            BigDecimal orderId = new BigDecimal(orderIdParam);

            int excute = jDDJ_Repository.AddOrderProcess(orderId, request.getProcessType()
                    , request.getRequest().getPos().getStoreId(), request.getRequest().getPos().getPosNo(), request.getRequest().getPos().getPosStream(), posTime
                    , request.getRequest().getOperator().getOperatorId(), request.getRequest().getOperator().getOperatorName(), request.getRequest().getOperator().getIP()
                    , request.getRequestTime(), request.getRequestIp(), conn, cstmt);

            if (excute <= 0) {
                log.error("SavePosProcess(); 新增订单处理流水失败，Pos请求数据：" + JsonUtil.objectToString(request));
                return false;
            } else {
                if (request.getProcessType() == 1) {
                    PosParam param = new PosParam();
                    param.setPosNo(request.getRequest().getPos().getPosNo());
                    param.setPosStream(request.getRequest().getPos().getPosStream());
                    param.setPosTime(simpleDateFormatnew.format(posTime));
                    String redisKey = "OrderPosParam:";

                    Jedis jedis = new Jedis(redisProperty.getHost());
                    jedis.select(Integer.valueOf(redisProperty.getDatabase()));
                    jedis.setex(redisKey + String.valueOf(orderId), 3600, JsonUtil.objectToString(param));//缓存1小时

                    // hashOperationsPos.put(redisKey, String.valueOf(orderId), JsonUtil.objectToString(param));
                }
            }
            logmess += "，结束时间 ：" + simpleDateFormat.format(new Date());
            log.info(logmess);
            return true;
        } catch (Exception ex) {
            log.error("SavePosProcess(); 异常出错，，Pos请求数据：" + JsonUtil.objectToString(request) + "，错误信息：" + ex.toString());
            return false;
        }
    }


    public boolean SaveAfsProcess(Connection conn, CallableStatement cstmt, AfsProcess request, String orderIdParam) {
        String message = "SaveAfsProcess(); 订单号：" + orderIdParam + "，开始时间 ：" + simpleDateFormat.format(new Date());
        log.info(message);
        try {
            Date posTime = StringUtils.isEmpty(request.getRequest().getPos().getPosTime()) ? new Date() : simpleDateFormatnew.parse(request.getRequest().getPos().getPosTime());

            BigDecimal orderId = new BigDecimal(orderIdParam);

            int excute = jDDJ_Repository.AfsConfirmReceipt(orderId, request.getDealtime(), request.getIp()
                    , request.getRequest().getPos().getStoreId(), request.getRequest().getPos().getPosNo(), request.getRequest().getPos().getPosStream(), posTime
                    , request.getRequest().getOperator().getOperatorId(), request.getRequest().getOperator().getOperatorName(), request.getRequest().getOperator().getIP(), conn, cstmt
            );

            if (excute <= 0) {
                log.error("SaveAfsProcess(); 售后已处理流水记录失败，Pos请求数据：" + JsonUtil.objectToString(request));
                return false;
            } else {

            }

            message += "，结束时间 ：" + dateFormatnew.format(new Date());

            log.info(message);
        } catch (Exception ex) {
            log.info("SaveAfsProcess(); 异常出错，，Pos请求数据：" + JsonUtil.objectToString(request) + "，错误信息：" + ex.toString());
            return false;
        }
        return true;

    }


    public boolean OrderInfoToDb(JDOrderQueryOrderMain jDOrderQueryOrderMain, OrderProcess orderProcess) throws SQLException {
        Connection conn = null;
        Savepoint savepoint1 = null;
        CallableStatement cstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
            conn.setAutoCommit(false);
            savepoint1 = conn.setSavepoint("Savepoint1");

            //过机成功的订单，不管订单状态，商品和促销信息都不会变更
            int excute = jDDJ_Repository.AddOrderMain(jDOrderQueryOrderMain, conn, cstmt);

            if (excute > 0) {
                //删除
                int temp_excute = 0;
                jDOrderQueryOrderMain.getOrderExtend().setOrderBuyerRemark(GetSubString(jDOrderQueryOrderMain.getOrderExtend().getOrderBuyerRemark(), 110));
                temp_excute = jDDJ_Repository.AddOrderExtend(jDOrderQueryOrderMain.getOrderExtend(), conn, cstmt);
                if (temp_excute <= 0) {
                    log.error("新增完结订单失败 AddOrderExtend，订单Id：" + jDOrderQueryOrderMain.getOrderId());
                    conn.rollback(savepoint1);
                    return false;
                }
                boolean productsave = jDDJ_Repository.AddOrderProductList(jDOrderQueryOrderMain.getOrderProductList(), conn, cstmt);
                if (productsave == false) {
                    log.error("新增完结订单失败 AddOrderProductList，订单Id：" + jDOrderQueryOrderMain.getOrderId());
                    conn.rollback(savepoint1);
                    return false;
                }
                boolean discountsave = jDDJ_Repository.AddOrderDiscountList(jDOrderQueryOrderMain.getOrderDiscountList(), conn, cstmt);
                if (discountsave == false) {
                    log.error("新增完结订单失败 AddOrderDiscountList，订单Id：" + jDOrderQueryOrderMain.getOrderId());
                    conn.rollback(savepoint1);
                    return false;
                }
                log.error("新增完结订单失败 AddOrderMain，订单Id：" + jDOrderQueryOrderMain.getOrderId());
                conn.rollback(savepoint1);
                return false;
            }

            //保存过机流水
            if (!SavePosProcess(conn, cstmt, orderProcess, jDOrderQueryOrderMain.getOrderId().toString())) {
                log.error("新增过机流水失败，订单Id：" + jDOrderQueryOrderMain.getOrderId().toString());
                conn.rollback(savepoint1);
                return false;
            }


            conn.commit();
            return true;
        } catch (Exception ex) {
            log.error(ex.toString());
            conn.rollback(savepoint1);
            return false;
        } finally {
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (Exception ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
        }

    }


    public String getNewToken() {
        ApiToken token = apiTokenRepository.getNewToken();
        return token.getToken();
    }


}
