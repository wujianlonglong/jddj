package com.example.business.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shilhu on 2016/10/27.
 */
@Repository
public class JDDJ_Repository {

    @Autowired
    protected JdbcTemplate jingDongDaoJiaJdbcTemplate;

    /**
     * query all jingdong dao jia shop products price record from DB.
     *
     * @return List<JingDongProductPricePO>
     */
    public Map<String, JingDongProductPricePO> getJinDongShopList() {
        String JINGDONG_SHOP_LIST_SQL = "SELECT STORE_ID, GOODS_ID, BATCH_SALE_PRICE, BATCH_MEMBER_PRICE, BATCH_MARKET_PRICE, MODIFY_TIME, IS_SYNC FROM GOODS_PRICE_SYNC ORDER BY STORE_ID";
        List<JingDongProductPricePO> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_SHOP_LIST_SQL, getRowMapper());
        Map tmpMap = new HashMap<String, JingDongProductPricePO>();
        for (JingDongProductPricePO obj : list) {
            String key = obj.getStoreId() + "_" + obj.getGoodsId();
            tmpMap.put(key, obj);

        }
        return tmpMap;
    }


    public Map<String, JingDongProductPricePO> getJinDongShopListByStoreId(String StoreId) {
        String JINGDONG_SHOP_LIST_SQL = "SELECT STORE_ID, GOODS_ID, BATCH_SALE_PRICE, BATCH_MEMBER_PRICE, BATCH_MARKET_PRICE, MODIFY_TIME, IS_SYNC FROM GOODS_PRICE_SYNC WHERE STORE_ID=" + StoreId;
        List<JingDongProductPricePO> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_SHOP_LIST_SQL, getRowMapper());

        Map tmpMap = new HashMap<String, JingDongProductPricePO>();
        for (JingDongProductPricePO obj : list) {
            String key = obj.getGoodsId();
            tmpMap.put(key, obj);
        }
        return tmpMap;
    }


    public JDOrderInfoQueryResponseOrderMain GetLocalOrderMain(BigDecimal orderId) {
        String JINGDONG_ORDER_MAIN_SQL = "SELECT * FROM ORDER_MAIN where order_id=" + String.valueOf(orderId);
        List<JDOrderInfoQueryResponseOrderMain> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_ORDER_MAIN_SQL, getRowMapperOrderMain());

        JDOrderInfoQueryResponseOrderMain jDOrderInfoQueryResponseOrderMain = new JDOrderInfoQueryResponseOrderMain();
        if (list != null && list.size() > 0) {
            jDOrderInfoQueryResponseOrderMain = list.get(0);
        }

        return jDOrderInfoQueryResponseOrderMain;
    }

    private RowMapper<JDOrderInfoQueryResponseOrderMain> getRowMapperOrderMain() {
        return (resultSet, i) -> {
            JDOrderInfoQueryResponseOrderMain jDOrderInfoQueryResponseOrderMain = new JDOrderInfoQueryResponseOrderMain();
            jDOrderInfoQueryResponseOrderMain.setOrderId(resultSet.getBigDecimal("Order_Id"));
            jDOrderInfoQueryResponseOrderMain.setOrderStatus(resultSet.getInt("Order_Status"));
            jDOrderInfoQueryResponseOrderMain.setDeliveryType(resultSet.getInt("Delivery_Type"));
            jDOrderInfoQueryResponseOrderMain.setProduceStationNoIsv(resultSet.getString("Produce_Station_No_Isv"));
            jDOrderInfoQueryResponseOrderMain.setOrderDiscountMoney(resultSet.getInt("ORDER_DISCOUNT_MONEY"));
            jDOrderInfoQueryResponseOrderMain.setOrderTotalMoney(resultSet.getInt("Order_Total_Money"));
            return jDOrderInfoQueryResponseOrderMain;

        };

    }


    public List<JDOrderInfoQueryOrderDiscount> GetLocalOrderDiscountList(BigDecimal orderId) {
        String JINGDONG_ORDER_DISCOUNT_SQL = "SELECT * FROM ORDER_DISCOUNT where order_id=" + String.valueOf(orderId);
        List<JDOrderInfoQueryOrderDiscount> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_ORDER_DISCOUNT_SQL, getRowMapperOrderDiscount());
        return list;
    }

    private RowMapper<JDOrderInfoQueryOrderDiscount> getRowMapperOrderDiscount() {
        return (resultSet, i) -> {
            JDOrderInfoQueryOrderDiscount jDOrderInfoQueryOrderDiscount = new JDOrderInfoQueryOrderDiscount();
            jDOrderInfoQueryOrderDiscount.setDiscountType(resultSet.getInt("DISCOUNT_TYPE"));
            jDOrderInfoQueryOrderDiscount.setDiscountDetailType(resultSet.getInt("DISCOUNT_DETAIL_TYPE"));
            jDOrderInfoQueryOrderDiscount.setDiscountPrice(resultSet.getInt("DISCOUNT_PRICE"));
            return jDOrderInfoQueryOrderDiscount;

        };

    }


    public List<JDOrderQueryOrderProduct> GetLocalorderProductListt(BigDecimal orderId) {
        String JINGDONG_ORDER_PRODUCT_SQL = "SELECT * FROM  ORDER_PRODUCT where order_id=" + String.valueOf(orderId);
        List<JDOrderQueryOrderProduct> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_ORDER_PRODUCT_SQL, getRowMapperOrderProduct());

        return list;
    }

    private RowMapper<JDOrderQueryOrderProduct> getRowMapperOrderProduct() {
        return (resultSet, i) -> {
            JDOrderQueryOrderProduct jDOrderQueryOrderProduct = new JDOrderQueryOrderProduct();
            jDOrderQueryOrderProduct.setPromotionType(resultSet.getInt("promotion_Type"));
            jDOrderQueryOrderProduct.setSkuCostPrice(resultSet.getInt("sku_Cost_Price"));
            jDOrderQueryOrderProduct.setSkuId(resultSet.getInt("SKU_ID"));
            jDOrderQueryOrderProduct.setSkuIdIsv(resultSet.getString("SKU_ID_ISV"));
            jDOrderQueryOrderProduct.setSkuName(resultSet.getString("SKU_NAME"));
            jDOrderQueryOrderProduct.setSkuJdPrice(resultSet.getInt("SKU_JD_PRICE"));
            jDOrderQueryOrderProduct.setSkuCount(resultSet.getInt("SKU_COUNT"));
            return jDOrderQueryOrderProduct;

        };

    }


    public List<JDOrderInfoQueryResponseOassBussinessSku> GetLocaloassBussinessSkus(BigDecimal orderId) {
        String JINGDONG_ORDER_BUSSINESS_SKUS_SQL = "SELECT * FROM OASS_BUSSI_SKU  where order_id=" + String.valueOf(orderId);
        List<JDOrderInfoQueryResponseOassBussinessSku> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_ORDER_BUSSINESS_SKUS_SQL, getRowMapperOrderBussinessSkus());
        return list;
    }

    private RowMapper<JDOrderInfoQueryResponseOassBussinessSku> getRowMapperOrderBussinessSkus() {
        return (resultSet, i) -> {
            JDOrderInfoQueryResponseOassBussinessSku jDOrderInfoQueryResponseOassBussinessSku = new JDOrderInfoQueryResponseOassBussinessSku();
            jDOrderInfoQueryResponseOassBussinessSku.setPromotionPrice(resultSet.getInt("promotion_Price"));
            jDOrderInfoQueryResponseOassBussinessSku.setPdjPrice(resultSet.getInt("pdj_Price"));
            jDOrderInfoQueryResponseOassBussinessSku.setSkuId(resultSet.getLong("sku_Id"));
            jDOrderInfoQueryResponseOassBussinessSku.setOrderId(resultSet.getLong("ORDER_ID"));
            return jDOrderInfoQueryResponseOassBussinessSku;
        };

    }


    public List<JDOrderInfoQueryResponseOrderBussiDiscountMoney> GetLocaloassBussinessDiscount(long orderId, long skuId) {
        String JINGDONG_ORDER_BUSSINESS_DISCOUNT_SQL = "SELECT * FROM OASS_BUSSI_DISCOUNT where order_id=" + String.valueOf(orderId) + " and sku_id=" + String.valueOf(skuId);
        List<JDOrderInfoQueryResponseOrderBussiDiscountMoney> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_ORDER_BUSSINESS_DISCOUNT_SQL, getRowMapperOrderBussinessDiscount());
        return list;
    }

    private RowMapper<JDOrderInfoQueryResponseOrderBussiDiscountMoney> getRowMapperOrderBussinessDiscount() {
        return (resultSet, i) -> {
            JDOrderInfoQueryResponseOrderBussiDiscountMoney jDOrderInfoQueryResponseOrderBussiDiscountMoney = new JDOrderInfoQueryResponseOrderBussiDiscountMoney();
            jDOrderInfoQueryResponseOrderBussiDiscountMoney.setPromotionType(resultSet.getInt("PROMOTION_TYPE"));
            jDOrderInfoQueryResponseOrderBussiDiscountMoney.setPromotionCode(resultSet.getString("PROMOTION_CODE"));
            jDOrderInfoQueryResponseOrderBussiDiscountMoney.setSkuDiscountMoney(resultSet.getLong("SKU_DISCOUNT_MONEY"));
            return jDOrderInfoQueryResponseOrderBussiDiscountMoney;
        };

    }


    public String QueryOrderSkuPromotionCode(String orderId, String skuId) {
        String result = "";
        String JINGDONG_SKU_PROMOTION_SQL = "SELECT promotion_code FROM OASS_BUSSI_DISCOUNT where order_id=" + orderId + " and sku_Id=" + skuId;
        List<JDOrderInfoQueryResponseOrderBussiDiscountMoney> list = jingDongDaoJiaJdbcTemplate.query(JINGDONG_SKU_PROMOTION_SQL, getRowMappernew());
        if (list != null && list.size() > 0) {

            result = list.get(0).getPromotionCode();
        }
        return result;
    }


    private RowMapper<JDOrderInfoQueryResponseOrderBussiDiscountMoney> getRowMappernew() {
        return (resultSet, i) -> {
            JDOrderInfoQueryResponseOrderBussiDiscountMoney jDOrderInfoQueryResponseOrderBussiDiscountMoney = new JDOrderInfoQueryResponseOrderBussiDiscountMoney();
            jDOrderInfoQueryResponseOrderBussiDiscountMoney.setPromotionCode(resultSet.getString("promotion_code"));
            return jDOrderInfoQueryResponseOrderBussiDiscountMoney;
        };

    }

    /**
     * 配置rowmapper
     */
    private RowMapper<JingDongProductPricePO> getRowMapper() {
        return (resultSet, i) -> {
            JingDongProductPricePO jingDongProductPricePO = new JingDongProductPricePO();
            jingDongProductPricePO.setStoreId(resultSet.getString("STORE_ID"));
            jingDongProductPricePO.setGoodsId(resultSet.getString("GOODS_ID"));
            jingDongProductPricePO.setSalePrice(resultSet.getBigDecimal("BATCH_SALE_PRICE"));
            jingDongProductPricePO.setMarketPrice(resultSet.getBigDecimal("BATCH_MARKET_PRICE"));
            jingDongProductPricePO.setMemberPrice(resultSet.getBigDecimal("BATCH_MARKET_PRICE"));
            jingDongProductPricePO.setIsSync(resultSet.getInt("IS_SYNC"));
            return jingDongProductPricePO;
        };
    }


    private RowMapper<PosRequestOrderProcessParam> getPosMapper() {
        return (resultSet, i) -> {
            PosRequestOrderProcessParam posRequestOrderProcessParam = new PosRequestOrderProcessParam();
            posRequestOrderProcessParam.setOrderId(resultSet.getString("ORDER_ID"));
            return posRequestOrderProcessParam;
        };
    }


    private RowMapper<String> getOrderExceMapper() {
        return (resultSet, i) -> {
            String posRequestOrderProcessParam = new String();
            posRequestOrderProcessParam = resultSet.getString("order_id");
            return posRequestOrderProcessParam;
        };
    }


    public int insertBatchNo(String batchType, String batchNo, String requestTime) throws SQLException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.parse(requestTime);
        java.util.Date dtUtil = simpleDateFormat.parse(requestTime);
        Timestamp dt = new Timestamp(dtUtil.getTime());
        Connection conn = null;
        CallableStatement cstmt = null;
        conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_GOODS_MANAGE.PRC_SYNC_BATCH_NO_INSERT(?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setString(1, batchType);
        cstmt.setString(2, batchNo);
        cstmt.setTimestamp(3, dt);
        //注册输出参数
        cstmt.registerOutParameter(4, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(4);
        return value;
    }


    public void UpdateStockSyncBatchNo(List<JDBatchStockSync> tmpupdateHistoryList) throws ClassNotFoundException, SQLException {

        Connection conn = null;
        Class.forName("oracle.jdbc.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        conn.setAutoCommit(false);
        String sql = "Update GOODS_STOCK_SYNC Set BATCH_NO=?, BATCH_GOODS_STATUS=? , BATCH_STORE_STOCK=?, BATCH_CURRENT_STOCK=?  WHERE STORE_ID=? and GOODS_ID=?";
        PreparedStatement prest = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int x = 0; x < tmpupdateHistoryList.size(); x++) {
            prest.setString(1, tmpupdateHistoryList.get(x).getBatchno());
            prest.setBigDecimal(2, new BigDecimal(tmpupdateHistoryList.get(x).getGoodsStatus()));
            prest.setBigDecimal(3, tmpupdateHistoryList.get(x).getStoreStock());
            prest.setBigDecimal(4, tmpupdateHistoryList.get(x).getCurrentStock());
            prest.setString(5, tmpupdateHistoryList.get(x).getStoreid());
            prest.setBigDecimal(6, tmpupdateHistoryList.get(x).getGoodsid());
            prest.addBatch();
        }
        prest.executeBatch();
        conn.commit();
        conn.close();

    }


    public void UpdateSyncBatchNo(List<JDBatchPriceSync> jDBatchPriceSyncList) throws ClassNotFoundException, SQLException {

        Connection conn = null;
        Class.forName("oracle.jdbc.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        conn.setAutoCommit(false);
        String sql = "Update GOODS_PRICE_SYNC Set BATCH_NO=?, BATCH_SALE_PRICE=?, BATCH_MEMBER_PRICE=?, BATCH_MARKET_PRICE=? WHERE STORE_ID=? and GOODS_ID=?";
        PreparedStatement prest = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int x = 0; x < jDBatchPriceSyncList.size(); x++) {
            prest.setString(1, jDBatchPriceSyncList.get(x).getBatchno());
            prest.setBigDecimal(2, jDBatchPriceSyncList.get(x).getBatchsaleprice());
            prest.setBigDecimal(3, jDBatchPriceSyncList.get(x).getBatchmemberprice());
            prest.setBigDecimal(4, jDBatchPriceSyncList.get(x).getBatchmarketprice());
            prest.setString(5, jDBatchPriceSyncList.get(x).getStoreid());
            prest.setInt(6, jDBatchPriceSyncList.get(x).getGoodsid().intValue());
            prest.addBatch();
        }
        prest.executeBatch();
        conn.commit();
        conn.close();

    }


    public int AddOrderMain(JDOrderQueryOrderMain obj, Connection conn,CallableStatement cstmt) throws SQLException, ParseException {
        //  Connection conn = null;
        //CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_MAIN_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, obj.getOrderId());
        cstmt.setBigDecimal(2, new BigDecimal(String.valueOf(obj.getSrcSysId())));
        cstmt.setString(3, obj.getSrcOrderId());
        cstmt.setBigDecimal(4, new BigDecimal(obj.getSrcPlatId()));
        cstmt.setBigDecimal(5, new BigDecimal(obj.getSrcOrderType()));
        cstmt.setBigDecimal(6, new BigDecimal(obj.getSrcInnerType()));
        cstmt.setBigDecimal(7, new BigDecimal(obj.getSrcInnerOrderId()));
        cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getOrderType())));
        cstmt.setString(9, obj.getOrderJdSendpay());
        cstmt.setString(10, obj.getOrderBizUuid());
        cstmt.setBigDecimal(11, new BigDecimal(String.valueOf(obj.getOrderStockOwner())));
        cstmt.setBigDecimal(12, new BigDecimal(String.valueOf(obj.getOrderSkuType())));
        cstmt.setBigDecimal(13, new BigDecimal(String.valueOf(obj.getOrderStatus())));
        cstmt.setDate(14, new Date(obj.getOrderStatusTime().getTime()));
        cstmt.setDate(15, new Date(obj.getOrderStartTime().getTime()));
        cstmt.setDate(16, new Date(obj.getOrderPurchaseTime().getTime()));
        cstmt.setBigDecimal(17, new BigDecimal(String.valueOf(obj.getOrderAgingType())));
        cstmt.setDate(18, obj.getOrderPreDeliveryTime() == null ? null : new Date(obj.getOrderPreDeliveryTime().getTime()));
        cstmt.setDate(19, new Date(obj.getOrderPreStartDeliveryTime().getTime()));
        cstmt.setDate(20, new Date(obj.getOrderPreEndDeliveryTime().getTime()));
        cstmt.setDate(21, new Date(obj.getOrderCancelTime().getTime()));
        cstmt.setString(22, obj.getOrderCancelRemark());
        cstmt.setDate(23, obj.getOrderDeleteTime() == null ? null : new Date(obj.getOrderDeleteTime().getTime()));
        cstmt.setInt(24, obj.getOrderIsClosed() == false ? 0 : 1);
        cstmt.setDate(25, new Date(obj.getOrderCloseTime().getTime()));
        cstmt.setString(26, obj.getOrgCode());
        cstmt.setString(27, obj.getPopVenderId());
        cstmt.setBigDecimal(28, new BigDecimal(String.valueOf(obj.getBuyerPinType())));
        cstmt.setString(29, obj.getBuyerPin());
        cstmt.setString(30, obj.getBuyerNickName());
        cstmt.setString(31, obj.getBuyerFullName());
        cstmt.setString(32, obj.getBuyerFullAddress());
        cstmt.setString(33, obj.getBuyerTelephone());
        cstmt.setString(34, obj.getBuyerMobile());
        cstmt.setString(35, obj.getBuyerProvince());
        cstmt.setString(36, obj.getBuyerCity());
        cstmt.setString(37, obj.getBuyerCountry());
        cstmt.setString(38, obj.getBuyerTown());
        cstmt.setString(39, obj.getProduceStationNo());
        cstmt.setString(40, obj.getProduceStationName());
        cstmt.setString(41, obj.getProduceStationNoIsv());
        cstmt.setString(42, obj.getDeliveryStationNo());
        cstmt.setString(43, obj.getDeliveryStationName());
        cstmt.setString(44, obj.getDeliveryStationNoIsv());
        cstmt.setBigDecimal(45, new BigDecimal(String.valueOf(obj.getBuyerPinType())));
        cstmt.setString(46, obj.getDeliveryCarrierNo());
        cstmt.setString(47, obj.getDeliveryCarrierName());
        cstmt.setString(48, obj.getDeliveryBillNo());
        cstmt.setBigDecimal(49, obj.getDeliveryPackageWeight());
        cstmt.setBigDecimal(50, obj.getDeliveryPackageVolume());
        cstmt.setString(51, obj.getDeliveryManNo());
        cstmt.setString(52, obj.getDeliveryManName());
        cstmt.setString(53, obj.getDeliveryManPhone());
        cstmt.setDate(54, obj.getDeliveryConfirmTime() == null ? null : new Date(obj.getDeliveryConfirmTime().getTime()));
        cstmt.setBigDecimal(55, new BigDecimal(String.valueOf(obj.getOrderPayType())));
        cstmt.setString(56, obj.getOrderTakeSelfCode());
        cstmt.setBigDecimal(57, new BigDecimal(String.valueOf(obj.getOrderTotalMoney())));
        cstmt.setBigDecimal(58, new BigDecimal(String.valueOf(obj.getOrderDiscountMoney())));
        cstmt.setBigDecimal(59, new BigDecimal(String.valueOf(obj.getOrderFreightMoney())));
        cstmt.setBigDecimal(60, new BigDecimal(String.valueOf(obj.getOrderGoodsMoney())));
        cstmt.setBigDecimal(61, new BigDecimal(String.valueOf(obj.getOrderBuyerPayableMoney())));
        cstmt.setBigDecimal(62, new BigDecimal(String.valueOf(obj.getOrderVenderChargeMoney())));
        cstmt.setBigDecimal(63, new BigDecimal(String.valueOf(obj.getPackagingMoney())));
        cstmt.setBigDecimal(64, new BigDecimal(String.valueOf(obj.getOrderBalanceUsed())));
        cstmt.setBigDecimal(65, new BigDecimal(String.valueOf(obj.getOrderInvoiceOpenMark())));
        cstmt.setBigDecimal(66, new BigDecimal(String.valueOf(obj.getOrderFinanceOrgCode())));
        cstmt.setInt(67, obj.getIsJDGetCash() == false ? 0 : 1);
        cstmt.setInt(68, obj.getAdjustIsExists() == false ? 0 : 1);
        cstmt.setBigDecimal(69, new BigDecimal(String.valueOf(obj.getAdjustCount())));
        cstmt.setBigDecimal(70, new BigDecimal(String.valueOf(obj.getAdjustId())));
        cstmt.setDate(71, new Date(obj.getTs().getTime()));
        cstmt.setBigDecimal(72, new BigDecimal(String.valueOf(obj.getOrderJingdouMoney())));
        cstmt.setString(73, obj.getServiceManName());
        cstmt.setString(74, obj.getServiceManPhone());
        //注册输出参数
        cstmt.registerOutParameter(75, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(75);
        return value;
    }


    public int AddOrderInfoMain(JDOrderInfoQueryResponseOrderMain obj, Connection conn, CallableStatement cstmt) throws SQLException, ParseException {
        //Connection conn = null;
        // CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_MAIN_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, obj.getOrderId());
        cstmt.setBigDecimal(2, new BigDecimal(String.valueOf(obj.getSrcSysId())));
        cstmt.setString(3, obj.getSrcOrderId());
        cstmt.setBigDecimal(4, new BigDecimal(obj.getSrcPlatId()));
        cstmt.setBigDecimal(5, new BigDecimal(obj.getSrcOrderType()));
        cstmt.setBigDecimal(6, new BigDecimal(obj.getSrcInnerType()));
        cstmt.setBigDecimal(7, new BigDecimal(obj.getSrcInnerOrderId()));
        cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getOrderType())));
        cstmt.setString(9, obj.getOrderJdSendpay());
        cstmt.setString(10, obj.getOrderBizUuid());
        cstmt.setBigDecimal(11, new BigDecimal(String.valueOf(obj.getOrderStockOwner())));
        cstmt.setBigDecimal(12, new BigDecimal(String.valueOf(obj.getOrderSkuType())));
        cstmt.setBigDecimal(13, new BigDecimal(String.valueOf(obj.getOrderStatus())));
        cstmt.setDate(14, new Date(obj.getOrderStatusTime().getTime()));
        cstmt.setDate(15, new Date(obj.getOrderStartTime().getTime()));
        cstmt.setDate(16, new Date(obj.getOrderPurchaseTime().getTime()));
        cstmt.setBigDecimal(17, new BigDecimal(String.valueOf(obj.getOrderAgingType())));
        cstmt.setDate(18, obj.getOrderPreDeliveryTime() == null ? null : new Date(obj.getOrderPreDeliveryTime().getTime()));
        cstmt.setDate(19, new Date(obj.getOrderPreStartDeliveryTime().getTime()));
        cstmt.setDate(20, new Date(obj.getOrderPreEndDeliveryTime().getTime()));
        cstmt.setDate(21, obj.getOrderCancelTime() == null ? null : new Date(obj.getOrderCancelTime().getTime()));
        cstmt.setString(22, obj.getOrderCancelRemark());
        cstmt.setDate(23, obj.getOrderDeleteTime() == null ? null : new Date(obj.getOrderDeleteTime().getTime()));
        cstmt.setInt(24, obj.getOrderIsClosed() == false ? 0 : 1);
        cstmt.setDate(25, obj.getOrderCloseTime() == null ? null : new Date(obj.getOrderCloseTime().getTime()));
        cstmt.setString(26, obj.getOrgCode());
        cstmt.setString(27, obj.getPopVenderId());
        cstmt.setBigDecimal(28, new BigDecimal(String.valueOf(obj.getBuyerPinType())));
        cstmt.setString(29, obj.getBuyerPin());
        cstmt.setString(30, obj.getBuyerNickName());
        cstmt.setString(31, obj.getBuyerFullName());
        cstmt.setString(32, obj.getBuyerFullAddress());
        cstmt.setString(33, obj.getBuyerTelephone());
        cstmt.setString(34, obj.getBuyerMobile());
        cstmt.setString(35, obj.getBuyerProvince());
        cstmt.setString(36, obj.getBuyerCity());
        cstmt.setString(37, obj.getBuyerCountry());
        cstmt.setString(38, obj.getBuyerTown());
        cstmt.setString(39, obj.getProduceStationNo());
        cstmt.setString(40, obj.getProduceStationName());
        cstmt.setString(41, obj.getProduceStationNoIsv());
        cstmt.setString(42, obj.getDeliveryStationNo());
        cstmt.setString(43, obj.getDeliveryStationName());
        cstmt.setString(44, obj.getDeliveryStationNoIsv());
        cstmt.setBigDecimal(45, new BigDecimal(String.valueOf(obj.getBuyerPinType())));
        cstmt.setString(46, obj.getDeliveryCarrierNo());
        cstmt.setString(47, obj.getDeliveryCarrierName());
        cstmt.setString(48, obj.getDeliveryBillNo());
        cstmt.setBigDecimal(49, obj.getDeliveryPackageWeight());
        cstmt.setBigDecimal(50, obj.getDeliveryPackageVolume());
        cstmt.setString(51, obj.getDeliveryManNo());
        cstmt.setString(52, obj.getDeliveryManName());
        cstmt.setString(53, obj.getDeliveryManPhone());
        cstmt.setDate(54, obj.getDeliveryConfirmTime() == null ? null : new Date(obj.getDeliveryConfirmTime().getTime()));
        cstmt.setBigDecimal(55, new BigDecimal(String.valueOf(obj.getOrderPayType())));
        cstmt.setString(56, obj.getOrderTakeSelfCode());
        cstmt.setBigDecimal(57, new BigDecimal(String.valueOf(obj.getOrderTotalMoney())));
        cstmt.setBigDecimal(58, new BigDecimal(String.valueOf(obj.getOrderDiscountMoney())));
        cstmt.setBigDecimal(59, new BigDecimal(String.valueOf(obj.getOrderFreightMoney())));
        cstmt.setBigDecimal(60, new BigDecimal(String.valueOf(obj.getOrderGoodsMoney())));
        cstmt.setBigDecimal(61, new BigDecimal(String.valueOf(obj.getOrderBuyerPayableMoney())));
        cstmt.setBigDecimal(62, new BigDecimal(String.valueOf(obj.getOrderVenderChargeMoney())));
        cstmt.setBigDecimal(63, new BigDecimal(String.valueOf(obj.getPackagingMoney())));
        cstmt.setBigDecimal(64, new BigDecimal(String.valueOf(obj.getOrderBalanceUsed())));
        cstmt.setBigDecimal(65, new BigDecimal(String.valueOf(obj.getOrderInvoiceOpenMark())));
        cstmt.setBigDecimal(66, new BigDecimal(String.valueOf(obj.getOrderFinanceOrgCode())));
        cstmt.setInt(67, (obj.getIsJdGetcash() == null || obj.getIsJdGetcash() == false) ? 0 : 1);
        cstmt.setInt(68, obj.getAdjustIsExists() == false ? 0 : 1);
        cstmt.setBigDecimal(69, new BigDecimal(String.valueOf(obj.getAdjustCount())));
        cstmt.setBigDecimal(70, new BigDecimal(String.valueOf(obj.getAdjustId())));
        cstmt.setDate(71, new Date(obj.getTs().getTime()));
        cstmt.setBigDecimal(72, new BigDecimal(String.valueOf(obj.getOrderJingdouMoney())));
        cstmt.setString(73, obj.getServiceManName());
        cstmt.setString(74, obj.getServiceManPhone());
        //注册输出参数
        cstmt.registerOutParameter(75, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(75);
        return value;
    }


    public int AddOrderExtend(JDOrderQueryOrderExtend obj, Connection conn, CallableStatement cstmt) throws SQLException {
        //Connection conn = null;
        //CallableStatement cstmt = null;
        // conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_EXTEND_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, obj.getOrderId());
        cstmt.setString(2, obj.getBuyerProvinceName());
        cstmt.setString(3, obj.getBuyerCityName());
        cstmt.setString(4, obj.getBuyerCountryName());
        cstmt.setString(5, obj.getBuyerTownName());
        cstmt.setString(6, obj.getBuyerIp());
        cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getBuyerCoordType())));
        cstmt.setBigDecimal(8, obj.getBuyerLat());
        cstmt.setBigDecimal(9, obj.getBuyerLng());
        cstmt.setString(10, obj.getOrderInvoiceType());
        cstmt.setString(11, obj.getOrderInvoiceTitle());
        cstmt.setString(12, obj.getOrderInvoiceContent());
        cstmt.setString(13, obj.getOrderBuyerRemark());
        cstmt.setString(14, obj.getOrderVenderRemark());
        cstmt.setString(15, obj.getOrderDeliveryRemark());
        cstmt.setString(16, obj.getOrderCustomerServiceRemark());
        cstmt.setString(17, obj.getSpecialServiceTag());
        cstmt.setString(18, obj.getCartId());
        cstmt.setString(19, obj.getEquipmentId());
        cstmt.setBigDecimal(20, StringUtils.isEmpty(obj.getBusinessTagId()) ? BigDecimal.ZERO : new BigDecimal(obj.getBusinessTagId()));
        cstmt.setString(21, obj.getBusinessTag());
        cstmt.setString(22, obj.getBuyerPoi());
        cstmt.setString(23, obj.getOrdererName());
        cstmt.setString(24, obj.getOrdererMobile());
        //注册输出参数
        cstmt.registerOutParameter(25, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(25);
        return value;

    }


    public int AddOrderProduct(JDOrderQueryOrderProduct obj, Connection conn, CallableStatement cstmt) throws SQLException {
        //Connection conn = null;
        //CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_PRODUCT_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, obj.getOrderId());
        cstmt.setBigDecimal(2, obj.getAdjustId());
        cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuId())));
        cstmt.setString(4, obj.getSkuName());
        cstmt.setString(5, obj.getSkuIdIsv());
        cstmt.setBigDecimal(6, obj.getSkuSpuId());
        cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getSkuJdPrice())));
        cstmt.setString(8, String.valueOf(obj.getSkuCount()));
        cstmt.setString(9, String.valueOf(obj.getSkuStockOwner()));
        cstmt.setInt(10, obj.getIsGift() == false ? 0 : 1);
        cstmt.setBigDecimal(11, new BigDecimal(String.valueOf(obj.getAdjustMode())));
        cstmt.setString(12, obj.getSpecialServiceTag());
        cstmt.setString(13, obj.getUpcCode());
        cstmt.setString(14, obj.getCategoryId());
        cstmt.setString(15, obj.getSupplyShortCode());
        cstmt.setString(16, obj.getSupplyName());
        cstmt.setBigDecimal(17, new BigDecimal(String.valueOf(obj.getSkuStorePrice())));
        //cstmt.setBigDecimal(18,  BigDecimal.ZERO );
        cstmt.setBigDecimal(18, obj.getSkuCostPrice() == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(obj.getSkuCostPrice())));
        cstmt.setBigDecimal(19, obj.getPromotionType() == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(obj.getPromotionType())));
        //注册输出参数
        cstmt.registerOutParameter(20, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(20);
        return value;

    }


    public boolean AddOrderProductList(List<JDOrderQueryOrderProduct> jDOrderQueryOrderProductList, Connection conn, CallableStatement cstmt) throws SQLException {
        //Connection conn = null;
        //CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        boolean result = true;
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_PRODUCT_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        for (JDOrderQueryOrderProduct obj : jDOrderQueryOrderProductList) {
            cstmt.setBigDecimal(1, obj.getOrderId());
            cstmt.setBigDecimal(2, obj.getAdjustId());
            cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuId())));
            cstmt.setString(4, obj.getSkuName());
            cstmt.setString(5, obj.getSkuIdIsv());
            cstmt.setBigDecimal(6, obj.getSkuSpuId());
            cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getSkuJdPrice())));
            cstmt.setString(8, String.valueOf(obj.getSkuCount()));
            cstmt.setString(9, String.valueOf(obj.getSkuStockOwner()));
            cstmt.setInt(10, obj.getIsGift() == false ? 0 : 1);
            cstmt.setBigDecimal(11, new BigDecimal(String.valueOf(obj.getAdjustMode())));
            cstmt.setString(12, obj.getSpecialServiceTag());
            cstmt.setString(13, obj.getUpcCode());
            cstmt.setString(14, obj.getCategoryId());
            cstmt.setString(15, obj.getSupplyShortCode());
            cstmt.setString(16, obj.getSupplyName());
            cstmt.setBigDecimal(17, new BigDecimal(String.valueOf(obj.getSkuStorePrice())));
            //cstmt.setBigDecimal(18,  BigDecimal.ZERO );
            cstmt.setBigDecimal(18, obj.getSkuCostPrice() == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(obj.getSkuCostPrice())));
            cstmt.setBigDecimal(19, obj.getPromotionType() == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(obj.getPromotionType())));
            //注册输出参数
            cstmt.registerOutParameter(20, Types.INTEGER);
            cstmt.executeUpdate();
            int value = cstmt.getInt(20);
            if (value <= 0) {
                result = false;
                return result;
            }
        }
        return result;
    }


    public int AddOrderDiscount(JDOrderQueryOrderDiscount obj, Connection conn) throws SQLException {
        // Connection conn = null;
        CallableStatement cstmt = null;
        // conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_DISCOUNT_INSERT(?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, obj.getOrderId());
        cstmt.setBigDecimal(2, obj.getAdjustId());
        cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuId())));
        cstmt.setString(4, obj.getSkuIds());
        cstmt.setString(5, String.valueOf(obj.getDiscountType()));
        cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getDiscountDetailType())));
        cstmt.setString(7, obj.getDiscountCode());
        cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getDiscountPrice())));
        //注册输出参数
        cstmt.registerOutParameter(9, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(9);
        return value;

    }


    public boolean AddOrderDiscountList(List<JDOrderQueryOrderDiscount> jDOrderQueryOrderDiscountList, Connection conn,CallableStatement cstmt) throws SQLException {
        // Connection conn = null;
        //CallableStatement cstmt = null;
        // conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        boolean result=true;
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_DISCOUNT_INSERT(?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        for(JDOrderQueryOrderDiscount obj:jDOrderQueryOrderDiscountList) {
            cstmt.setBigDecimal(1, obj.getOrderId());
            cstmt.setBigDecimal(2, obj.getAdjustId());
            cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuId())));
            cstmt.setString(4, obj.getSkuIds());
            cstmt.setString(5, String.valueOf(obj.getDiscountType()));
            cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getDiscountDetailType())));
            cstmt.setString(7, obj.getDiscountCode());
            cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getDiscountPrice())));
            //注册输出参数
            cstmt.registerOutParameter(9, Types.INTEGER);
            cstmt.executeUpdate();
            int value = cstmt.getInt(9);
           if(value<=0)
           {
               result=false;
               return result;
           }
        }
        return result;
    }


    public int AddOrderInfoDiscount(JDOrderInfoQueryOrderDiscount obj, Connection conn, CallableStatement cstmt) throws SQLException {
        // Connection conn = null;
        //CallableStatement cstmt = null;
        // conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_DISCOUNT_INSERT(?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, new BigDecimal(String.valueOf(obj.getOrderId())));
        cstmt.setBigDecimal(2, BigDecimal.ZERO);
        cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuId())));
        cstmt.setString(4, obj.getSkuIds());
        cstmt.setString(5, String.valueOf(obj.getDiscountType()));
        cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getDiscountDetailType())));
        cstmt.setString(7, obj.getDiscountCode());
        cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getDiscountPrice())));
        //注册输出参数
        cstmt.registerOutParameter(9, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(9);
        return value;
    }


    public boolean AddOrderInfoDiscountList(List<JDOrderInfoQueryOrderDiscount> jDOrderInfoQueryOrderDiscountList, Connection conn, CallableStatement cstmt) throws SQLException {
        // Connection conn = null;
        //CallableStatement cstmt = null;
        // conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        boolean result = true;
        String procedure = "{call PKG_ORDER_MANAGE.PRC_ORDER_DISCOUNT_INSERT(?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);

        for (JDOrderInfoQueryOrderDiscount obj : jDOrderInfoQueryOrderDiscountList) {
            cstmt.setBigDecimal(1, new BigDecimal(String.valueOf(obj.getOrderId())));
            cstmt.setBigDecimal(2, BigDecimal.ZERO);
            cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuId())));
            cstmt.setString(4, obj.getSkuIds());
            cstmt.setString(5, String.valueOf(obj.getDiscountType()));
            cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getDiscountDetailType())));
            cstmt.setString(7, obj.getDiscountCode());
            cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getDiscountPrice())));
            //注册输出参数
            cstmt.registerOutParameter(9, Types.INTEGER);
            cstmt.executeUpdate();
            int value = cstmt.getInt(9);
            if (value <= 0) {
                result = false;
                return result;
            }
        }
        return result;
    }


    public int AddOassBussiSku(JDOrderInfoQueryResponseOassBussinessSku obj, Connection conn,CallableStatement cstmt) throws SQLException {
        // Connection conn = null;
        //CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_OASS_BUSSI_SKU_INSERT(?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, new BigDecimal(String.valueOf(obj.getOrderId())));
        cstmt.setBigDecimal(2, new BigDecimal(String.valueOf(obj.getSkuId())));
        cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getSkuCount())));
        cstmt.setBigDecimal(4, new BigDecimal(String.valueOf(obj.getPromotionPrice())));
        cstmt.setBigDecimal(5, new BigDecimal(String.valueOf(obj.getPdjPrice())));
        cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getCostPrice())));
        cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getCostRadio())));
        cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getSaleRadio())));
        cstmt.setBigDecimal(9, new BigDecimal(String.valueOf(obj.getSkuJdMoney())));
        cstmt.setBigDecimal(10, new BigDecimal(String.valueOf(obj.getJdSaleRedio())));
        //注册输出参数
        cstmt.registerOutParameter(11, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(11);
        return value;
    }


    public int AddOassBussiDiscount(BigDecimal orderId, BigDecimal skuId, JDOrderInfoQueryResponseOrderBussiDiscountMoney obj, Connection conn, CallableStatement cstmt ) throws SQLException {
        //Connection conn = null;
        // CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_ORDER_MANAGE.PRC_OASS_BUSSI_DISCOUNT_INSERT(?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, orderId);
        cstmt.setBigDecimal(2, skuId);
        cstmt.setString(3, String.valueOf(obj.getPromotionType()));
        cstmt.setBigDecimal(4, new BigDecimal(String.valueOf(obj.getPromotionDetailType())));
        cstmt.setBigDecimal(5, new BigDecimal(String.valueOf(obj.getPromotionCode())));
        cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getSkuDiscountMoney())));
        cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getSaleRadio())));
        //注册输出参数
        cstmt.registerOutParameter(8, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(8);
        return value;
    }


    public boolean AddOassBussiDiscountList(BigDecimal orderId, BigDecimal skuId, List<JDOrderInfoQueryResponseOrderBussiDiscountMoney> jDOrderInfoQueryResponseOrderBussiDiscountMoneyList, Connection conn, CallableStatement cstmt ) throws SQLException {
        //Connection conn = null;
        // CallableStatement cstmt = null;
        //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        boolean result=true;
        String procedure = "{call PKG_ORDER_MANAGE.PRC_OASS_BUSSI_DISCOUNT_INSERT(?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        for(JDOrderInfoQueryResponseOrderBussiDiscountMoney obj:jDOrderInfoQueryResponseOrderBussiDiscountMoneyList) {
            cstmt.setBigDecimal(1, orderId);
            cstmt.setBigDecimal(2, skuId);
            cstmt.setString(3, String.valueOf(obj.getPromotionType()));
            cstmt.setBigDecimal(4, new BigDecimal(String.valueOf(obj.getPromotionDetailType())));
            cstmt.setBigDecimal(5, new BigDecimal(String.valueOf(obj.getPromotionCode())));
            cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getSkuDiscountMoney())));
            cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getSaleRadio())));
            //注册输出参数
            cstmt.registerOutParameter(8, Types.INTEGER);
            cstmt.executeUpdate();
            int value = cstmt.getInt(8);
            if(value<=0)
            {
                result=false;
                return result;
            }

        }
        return result;
    }


    public int AddOrderProcess(BigDecimal orderId, int processType
            , String storeId, String posNo, String posStream, java.util.Date posTime
            , String operatorId, String operatorName, String operatorIp, java.util.Date requestTime, String requestIp,Connection conn, CallableStatement cstmt) throws SQLException {
       // Connection conn = null;
       // CallableStatement cstmt = null;
       // conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PRC_ORDER_PROCESS_INSERT(?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, orderId);
        cstmt.setBigDecimal(2, new BigDecimal(String.valueOf(processType)));
        cstmt.setString(3, storeId);
        cstmt.setString(4, posNo);
        cstmt.setString(5, posStream);
        cstmt.setDate(6, processType == 0 ? null : new Date(posTime.getTime()));
        cstmt.setString(7, operatorId);
        cstmt.setString(8, operatorName);
        cstmt.setString(9, operatorIp);
        cstmt.setDate(10, new Date(requestTime.getTime()));
        cstmt.setString(11, requestIp);
        //注册输出参数
        cstmt.registerOutParameter(12, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(12);
        return value;
    }


    public int AfsConfirmReceipt(BigDecimal orderId, java.util.Date dealTime, String dealIp, String storeId
            , String posNo, String posStream, java.util.Date posTime
            , String operatorId, String operatorName, String operatorIp,Connection conn ,CallableStatement cstmt) throws SQLException {
      //Connection conn = null;
      //CallableStatement cstmt = null;
      //conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PRC_AFS_CONFIRM_RECEIPT(?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, orderId);
        cstmt.setDate(2, new Date(dealTime.getTime()));
        cstmt.setString(3, dealIp);
        cstmt.setString(4, storeId);
        cstmt.setString(5, posNo);
        cstmt.setString(6, posStream);
        cstmt.setDate(7, new Date(posTime.getTime()));
        cstmt.setString(8, operatorId);
        cstmt.setString(9, operatorName);
        cstmt.setString(10, operatorIp);
        //注册输出参数
        cstmt.registerOutParameter(11, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(11);
        return value;
    }


    public int SercherPosOrder(String orderId, int type) {
        String strSql = "SELECT * FROM ORDER_PROCESS where order_id=" + orderId + " and process_type=" + type;
        List<PosRequestOrderProcessParam> list = jingDongDaoJiaJdbcTemplate.query(strSql, getPosMapper());

        if (null != list && list.size() > 0)
            return list.size();
        else
            return 0;
    }


    public boolean QueryIsOrderException(String orderId, int type) {
        String strSql = "SELECT * FROM ORDER_EXCEPTION where order_id=" + orderId + " and process_type=" + type;
        List<String> list = jingDongDaoJiaJdbcTemplate.query(strSql, getOrderExceMapper());

        if (null != list && list.size() > 0)
            return true;
        else
            return false;
    }


    public int AddAfsMain(JDAfsResponseResult obj, Connection conn ,CallableStatement cstmt ) throws SQLException {
        //Connection conn = null;
      //  CallableStatement cstmt = null;
      //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_AFS_MANAGE.PRC_MAIN_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, obj.getOrderId());
        cstmt.setBigDecimal(2, obj.getAfsServiceOrder());
        cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getAfsServiceState())));
        cstmt.setDate(4, new Date(obj.getApprovedDate().getTime()));
        cstmt.setString(5, obj.getApprovePin());
        cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getQuestionTypeCid())));
        cstmt.setString(7, obj.getQuestionDesc());
        cstmt.setString(8, obj.getCustomerPin());
        cstmt.setString(9, obj.getPickupDetail());
        cstmt.setString(10, obj.getCustomerName());
        cstmt.setString(11, obj.getCustomerMobilePhone());
        cstmt.setString(12, obj.getPickwareAddress());
        cstmt.setString(13, obj.getCarriersNo());
        cstmt.setString(14, obj.getDeliveryNo());
        cstmt.setString(15, obj.getStationId());
        cstmt.setString(16, obj.getStationName());
        cstmt.setDate(17, new Date(obj.getPickupStartTime().getTime()));
        cstmt.setDate(18, new Date(obj.getPickupEndTime().getTime()));
        cstmt.setBigDecimal(19, new BigDecimal(String.valueOf(obj.getOrderAging())));
        cstmt.setBigDecimal(20, new BigDecimal(String.valueOf(obj.getCashMoney())));
        cstmt.setBigDecimal(21, new BigDecimal(String.valueOf(obj.getPayType())));
        cstmt.setBigDecimal(22, new BigDecimal(String.valueOf(obj.getAfsMoney())));
        cstmt.setBigDecimal(23, new BigDecimal(String.valueOf(obj.getOrderFreightMoney())));
        cstmt.setBigDecimal(24, new BigDecimal(String.valueOf(obj.getJdBeansMoney())));
        cstmt.setBigDecimal(25, new BigDecimal(String.valueOf(obj.getVirtualMoney())));
        cstmt.setString(26, obj.getApplyDeal());
        cstmt.setBigDecimal(27, new BigDecimal(String.valueOf(obj.getDutyAssume())));
        cstmt.setBigDecimal(28, new BigDecimal(String.valueOf(obj.getOrderStatus())));
        cstmt.setString(29, obj.getDeliveryState());
        cstmt.setString(30, obj.getDeliveryMan());
        cstmt.setString(31, obj.getDeliveryMobile());
        cstmt.setString(32, obj.getDeliveryManNo());
        cstmt.setBigDecimal(33, new BigDecimal(String.valueOf(obj.getOrderType())));
        //注册输出参数
        cstmt.registerOutParameter(34, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(34);
        return value;
    }


    public int AddAfsDetail(BigDecimal afsServiceOrder, JDAfsResponseAfsDetail obj, Connection conn ,CallableStatement cstmt ) throws SQLException {
       // Connection conn = null;
        //CallableStatement cstmt = null;
        //conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_AFS_MANAGE.PRC_DETAIL_INSERT(?,?,?,?,?,?,?,?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, afsServiceOrder);
        cstmt.setBigDecimal(2, new BigDecimal(String.valueOf(obj.getWareId())));
        cstmt.setString(3, obj.getWareName());
        cstmt.setString(4, obj.getSkuIdIsv());
        cstmt.setBigDecimal(5, new BigDecimal(String.valueOf(obj.getPayPrice())));
        cstmt.setBigDecimal(6, new BigDecimal(String.valueOf(obj.getSkuCount())));
        cstmt.setBigDecimal(7, new BigDecimal(String.valueOf(obj.getSkuMoney())));
        cstmt.setBigDecimal(8, new BigDecimal(String.valueOf(obj.getAfsMoney())));
        cstmt.setBigDecimal(9, new BigDecimal(String.valueOf(obj.getCashMoney())));
        cstmt.setBigDecimal(10, new BigDecimal(String.valueOf(obj.getJdBeansMoney())));
        cstmt.setBigDecimal(11, new BigDecimal(String.valueOf(obj.getVirtualMoney())));
        //注册输出参数
        cstmt.registerOutParameter(12, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(12);
        return value;

    }


    public int AddAfsDiscount(BigDecimal afsServiceOrder, BigDecimal wareId, JDAfsResponseAfsDiscount obj, Connection conn ,CallableStatement cstmt ) throws SQLException {
       // Connection conn = null;
       // CallableStatement cstmt = null;
      //  conn = DriverManager.getConnection("jdbc:oracle:thin:@193.0.10.53:1521:sjos", "jddj", "jddj");
        String procedure = "{call PKG_AFS_MANAGE.PRC_DISCOUNT_INSERT(?,?,?,?,?)}";
        cstmt = conn.prepareCall(procedure);
        cstmt.setBigDecimal(1, afsServiceOrder);
        cstmt.setBigDecimal(2, wareId);
        cstmt.setBigDecimal(3, new BigDecimal(String.valueOf(obj.getDiscountType())));
        cstmt.setBigDecimal(4, new BigDecimal(String.valueOf(obj.getDiscountMoney())));
        //注册输出参数
        cstmt.registerOutParameter(5, Types.INTEGER);
        cstmt.executeUpdate();
        int value = cstmt.getInt(5);
        return value;
    }

}
