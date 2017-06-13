package com.example.business;

import com.example.business.model.*;
import com.example.domain.sjhub.*;
import com.example.repository.sjhub.PlatformProductRepository;
import com.example.repository.sjhub.PlatformShopRepository;
import com.example.repository.sjhub.PriceImportRepository;
import com.example.utils.constant.RedisConstant;
import com.example.utils.enums.ProductStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Created by gaoqichao on 16-7-24.
 * <p>
 * 库存基本业务处理类
 */
@Component
public class BaseStockBusiness {
    private static final Logger log = LoggerFactory.getLogger(BaseStockBusiness.class);


    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Map<String, String>> valueOperations;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Timestamp> lastSyncStockTiemOperations;

    @Resource(name = "redisTemplate")
    protected ValueOperations<String, Timestamp> lastSyncPriceTiemOperations;


    @Autowired
    protected PlatformProductRepository platProductRepository;


    @Autowired
    protected PriceImportRepository priceImportRepository;

    @Autowired
    PlatformShopRepository shopRepository;

    @Autowired
    protected JdbcTemplate jingDongDaoJiaJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    protected JdbcTemplate stockJdbcTemplate;


    private static final String UPDATE_STOCK_SYNCFLAG_SQL = "Update GOODS_STOCK_SYNC Set BATCH_NO=?, BATCH_STORE_STOCK=?, BATCH_CURRENT_STOCK=?,SYNC_TIME=?  WHERE STORE_ID=? and GOODS_ID=?";


    private static final String UPDATE_PRICE_SYNCFLAG_SQL = "Update GOODS_PRICE_SYNC Set BATCH_NO=?, BATCH_SALE_PRICE=?, BATCH_MEMBER_PRICE=?, BATCH_MARKET_PRICE=?,SYNC_TIME=?  WHERE STORE_ID=? and GOODS_ID=?";


    /**
     * 全部库存sql
     */
    private static final String ALL_PRODUCT_STOCK_SQL = "SELECT m.scbh, m.glbh, m.kcsl, n.goodsstatus " +
            "FROM xt_kcb m, future.goodsshop n " +
            "WHERE m.scbh=n.shopid and m.glbh=n.goodsid ";
    /**
     * 取得门店下的所有的数据
     */
    private static final String SHO_PRODUCT_STOCK_SQL = String.format("%s and m.scbh =? ", ALL_PRODUCT_STOCK_SQL);


    private static final String SHOP_PRODUCT_STOCK_SQL = String.format("%s and m.scbh=? and m.glbh=?", ALL_PRODUCT_STOCK_SQL);


    /**
     * 指定了门店列表当天更新的商品库存信息
     */
    private static final String SHOP_LIST_PRODUCT_STOCK_UPDATE_SQL = String.format("%s AND m.SCBH in (:shopCodeList) AND  m.GXRQ >= :syncTimeStamp", ALL_PRODUCT_STOCK_SQL);

    /**
     * 指定了门店列表当天新增的商品库存信息
     */
    private static final String SHOP_LIST_PRODUCT_STOCK_ADD_SQL = String.format("%s AND m.SCBH in (:shopCodeList) AND  m.GXRQ IS NULL AND m.RQ >= :syncTimeStamp", ALL_PRODUCT_STOCK_SQL);


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    protected Map<String, String> getPlatShopMap(String platformId) {
        List<PlatformShop> platformShopList = shopRepository.findByPlatformIdAndStatus(platformId, 1);
        Map<String, String> tempMap = new HashMap<>();
        platformShopList.forEach(platformShop -> {
            tempMap.put(platformShop.getSjShopCode(), platformShop.getPlatformShopCode());
        });
        return tempMap;
    }


    /**
     * 取得平台门店与三江门店的编码映射
     *
     * @param platformId 平台id
     * @return 平台与三江门店的编码映射
     */
    public Map<String, String> getPlatProdMap(String platformId, boolean isForSj) {
        String redisKey = isForSj ? RedisConstant.PLAT_PROD_MAP_SJ + platformId : RedisConstant.PLAT_PROD_MAP_PLAT + platformId;
        Map<String, String> platProdMap = valueOperations.get(redisKey);
        if (MapUtils.isEmpty(platProdMap)) {
            synchronized (BaseStockBusiness.class) {
                platProdMap = valueOperations.get(redisKey);
                if (MapUtils.isEmpty(platProdMap)) {
                    // 取得所有上架状态商品
                    List<PlatformProduct> platProdList = platProductRepository.findByPlatformIdAndStatus(platformId, 1);
                    // 三江商品编码与平台商品编码的映射
                    Map<String, String> tempMap = new HashMap<>();
                    platProdList.stream()
                            .filter(product -> product.getStatus() != ProductStatusEnum.DELETED.status())
                            .forEach(product -> {
                                if (isForSj) {
                                    tempMap.put(product.getSjGoodsCode(), product.getPlatformGoodsCode());
                                } else {
                                    tempMap.put(product.getPlatformGoodsCode(), product.getSjGoodsCode());
                                }
                            });

                    valueOperations.set(redisKey, tempMap);
                    platProdMap=valueOperations.get(redisKey);
                }
            }
        }
        return platProdMap;
    }


    /**
     * 批量更新京东到家库存同步表
     *
     * @param list 需要同步的商品list
     */
    public void bateUpdateStockSyncJddj(List<JDBatchStockSync> list) {
        try {
            jingDongDaoJiaJdbcTemplate.batchUpdate(UPDATE_STOCK_SYNCFLAG_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    JDBatchStockSync stock = (JDBatchStockSync) list.get(i);
                    ps.setString(1, stock.getBatchno());
                    ps.setBigDecimal(2, stock.getStoreStock());
                    ps.setBigDecimal(3, stock.getCurrentStock());
                    ps.setTimestamp(4, new Timestamp(stock.getSyncTime().getTime()));
                    ps.setString(5, stock.getStoreid());
                    ps.setBigDecimal(6, stock.getGoodsid());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
        } catch (Exception ex) {
            log.error("批量更新至本地库存同步表报错：" + ex.toString());
            return;

        }
    }


    /**
     * 批量更新京东到家价格同步表
     *
     * @param list 需要同步的商品list
     */
    public void bateUpdatePriceSyncJddj(List<JDBatchPriceSync> list) {
        try {
            jingDongDaoJiaJdbcTemplate.batchUpdate(UPDATE_PRICE_SYNCFLAG_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    JDBatchPriceSync stock = (JDBatchPriceSync) list.get(i);
                    ps.setString(1, stock.getBatchno());
                    ps.setBigDecimal(2, stock.getBatchsaleprice());
                    ps.setBigDecimal(3, stock.getBatchmemberprice());
                    ps.setBigDecimal(4, stock.getBatchmarketprice());
                    ps.setTimestamp(5, new Timestamp(stock.getSyncTime().getTime()));
                    ps.setString(6, stock.getStoreid());
                    ps.setBigDecimal(7, stock.getGoodsid());
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
        } catch (Exception ex) {
            log.error("批量更新至本地价格同步表报错：" + ex.toString());
            return;
        }
    }


    public XtStore getOneXtStockList(String shopCode, String goodCode) {
        try {
            return stockJdbcTemplate.queryForObject(SHOP_PRODUCT_STOCK_SQL, new Object[]{shopCode, goodCode}, getRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            //size=null||size=0
            log.error("中间库未找到商场" + shopCode + ",商品" + goodCode);
            return null;
        } catch (IncorrectResultSizeDataAccessException ex) {
            //size>1
            log.error("中间库中存在多条相同数据商场" + shopCode + ",商品" + goodCode);
            return null;
        }
    }


    /**
     * 取得给定门店列表下的所有商品库存同步数据列表
     *
     * @param shopCode 门店编码列表
     * @return 库存同步数据列表
     */
    public List<XtStore> getXtStockList(String shopCode) {
        return stockJdbcTemplate.query(SHO_PRODUCT_STOCK_SQL, new Object[]{shopCode}, getRowMapper());
//        return stockJdbcTemplate.query(SHO_PRODUCT_STOCK_SQL, new Object[]{shopCode}, new RowMapper<XtStore>(){
//            public XtStore mapRow(ResultSet resultSet, int i) throws SQLException {
//                int status = resultSet.getInt("GOODSSTATUS");
//                if (status != 1 && status != 2 && status != 3 && status != 4 && status != 7) {
//                    return null;
//                }
//                XtStore xtStore = new XtStore();
//                xtStore.setSjShopCode(resultSet.getString("SCBH"));
//                xtStore.setSjGoodsCode(resultSet.getString("GLBH"));
//                xtStore.setStockNumber(resultSet.getInt("KCSL"));
//                return xtStore;
//            }
//
//        });
    }


    /**
     * 配置rowmapper
     */
    private RowMapper<XtStore> getRowMapper() {
        return (resultSet, i) -> {
            int status = resultSet.getInt("GOODSSTATUS");
            if (status != 1 && status != 2 && status != 3 && status != 4 && status != 7) {
                return null;
            }
            XtStore xtStore = new XtStore();
            xtStore.setSjShopCode(resultSet.getString("SCBH"));
            xtStore.setSjGoodsCode(resultSet.getString("GLBH"));
            xtStore.setStockNumber(resultSet.getInt("KCSL"));
            xtStore.setStatus(status);
            return xtStore;
        };
    }


    /**
     * 取得给定门店列表下的所有商品库存同步数据列表
     *
     * @param shopCodeList 门店编码列表
     * @return 库存同步数据列表
     */
    protected List<XtStore> getXtStockList(List<String> shopCodeList, LocalDateTime syncTime) {
        Timestamp syncTimeStamp = lastSyncStockTiemOperations.get(RedisConstant.LAST_STOCK_SYNCJDDJ_TIME);

        if (null == syncTimeStamp) {
            syncTimeStamp = Timestamp.valueOf(syncTime.toLocalDate().atStartOfDay());
        }

        Map<String, Object> params = new HashMap<>();
        params.put("shopCodeList", shopCodeList);
        params.put("syncTimeStamp", syncTimeStamp);

        NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(stockJdbcTemplate);

        // 取得更新库存信息
        List<XtStore> updateStoreList = jdbc.query(SHOP_LIST_PRODUCT_STOCK_UPDATE_SQL,
                params, getRowMapper());
        // 取得新增库存信息
        List<XtStore> addStoreList = jdbc.query(SHOP_LIST_PRODUCT_STOCK_ADD_SQL,
                params, getRowMapper());

        return (List<XtStore>) CollectionUtils.union(updateStoreList, addStoreList);
    }


    /**
     * 每个门店的价格导入数据商品编码列表映射
     *
     * @return 门店-商品编码列表映射
     */
    protected Map<String, List<String>> getPriceImportMapGroupByShop() {
        Map<String, List<String>> map = new HashMap<>();
        List<PriceImport> priceImportList = priceImportRepository.findAll();
        if (CollectionUtils.isNotEmpty(priceImportList)) {
            List<String> goodsCodeList;
            for (PriceImport priceImport : priceImportList) {
                goodsCodeList = map.get(priceImport.getSjShopCode());
                if (CollectionUtils.isEmpty(goodsCodeList)) {
                    goodsCodeList = new ArrayList<>();
                }
                goodsCodeList.add(priceImport.getSjGoodsCode());
                map.put(priceImport.getSjShopCode(), goodsCodeList);
            }
        }

        return map;
    }


    /**
     * 获取门店商品市场价
     */
    private static final String SQL_QUERY_MARKT_PRICE = "select goodsid, normalprice from GOODSSHOP@CSHQ where shopid= ?" +
            "and goodsid in (select to_number(glbh) from JDDJ_GOODS_SYNC )";


    /**
     * 取得门店内的所有商品的市场价格信息
     *
     * @param sjShopCode 门店编码
     * @return 商品价格列表
     */
    protected List<MarktPrice> getGoodsMarktPriceList(String sjShopCode) {
        return stockJdbcTemplate.query(SQL_QUERY_MARKT_PRICE, new Object[]{sjShopCode}, getMarktPriceRowMapper());
    }

    /**
     * 配置rowmapper
     */
    private RowMapper<MarktPrice> getMarktPriceRowMapper() {
        return (resultSet, i) -> {
            MarktPrice marktPrice = new MarktPrice();
            marktPrice.setGoodCode(resultSet.getString("goodsid"));
            marktPrice.setMarktPrice(resultSet.getBigDecimal("normalprice"));
            return marktPrice;
        };
    }


    private static final String SQL_PRICE_SYNC = "select * from GOODS_PRICE_SYNC where store_id=?";

    public List<JDBatchPriceSync> getPriceSync(String sjShopCode) {
        return jingDongDaoJiaJdbcTemplate.query(SQL_PRICE_SYNC, new Object[]{sjShopCode}, getPriceSyncRowMapper());
    }

    public RowMapper<JDBatchPriceSync> getPriceSyncRowMapper() {
        return (resultSet, i) -> {
            JDBatchPriceSync obj = new JDBatchPriceSync();
            obj.setStoreid(resultSet.getString("store_id"));
            obj.setGoodsid(new BigDecimal(resultSet.getString("goods_id")));
            obj.setBatchsaleprice(resultSet.getBigDecimal("BATCH_SALE_PRICE"));
            obj.setBatchmarketprice(resultSet.getBigDecimal("BATCH_MARKET_PRICE"));
            return obj;
        };
    }

    private static final String SQL_QUERY_MARKT_PRICE_ONE = "select goodsid, normalprice from GOODSSHOP@CSHQ where shopid= ? and goodsid= ? ";

    /**
     * 取得门店某个商品的市场价格信息
     *
     * @param sjShopCode 门店编码
     * @param sjGoodCode 商品编码
     * @return 商品价格列表
     */
    public List<MarktPrice> getGoodsMarktPrice(String sjShopCode, String sjGoodCode) {
        return stockJdbcTemplate.query(SQL_QUERY_MARKT_PRICE_ONE, new Object[]{sjShopCode, sjGoodCode}, getMarktPriceRowMapperNew());
    }

    /**
     * 配置rowmapper
     */
    private RowMapper<MarktPrice> getMarktPriceRowMapperNew() {
        return (resultSet, i) -> {
            MarktPrice marktPrice = new MarktPrice();
            marktPrice.setGoodCode(resultSet.getString("goodsid"));
            marktPrice.setMarktPrice(resultSet.getBigDecimal("normalprice"));
            return marktPrice;
        };
    }


    /**
     * 取得门店所有商品的库存信息
     */
    private static final String SQL_QUERY_PRICE_BY_SHOP = "SELECT  t.GLBH,t.SCBH,t.JZSJ,t.JZHYJ FROM XT_LWXX t WHERE t.SCBH = ? " +
            " AND exists(SELECT  1 FROM PLATFORM_PRODUCT s WHERE  s.PLATFORM_ID=10002  and t.GLBH = s.SJ_GOODS_CODE)";

    /**
     * 取得门店内的所有商品的价格信息
     *
     * @param sjShopCode 门店编码
     * @return 商品价格列表
     */
    protected List<GoodsPrice> getGoodsPriceListByShop(String sjShopCode) {
        return jdbcTemplate.query(SQL_QUERY_PRICE_BY_SHOP, new Object[]{sjShopCode}, getPriceRowMapper());
    }

    /**
     * 配置rowmapper
     */
    private RowMapper<GoodsPrice> getPriceRowMapper() {
        return (resultSet, i) -> {
            GoodsPrice goodsPrice = new GoodsPrice();
            goodsPrice.setGoodsCode(resultSet.getString("GLBH"));
            goodsPrice.setShopCode(resultSet.getString("SCBH"));
            goodsPrice.setSalePrice(resultSet.getBigDecimal("JZSJ"));
            goodsPrice.setMemberPrice(resultSet.getBigDecimal("JZHYJ"));
            return goodsPrice;
        };
    }

    private static final String SQL_QUERY_INCREMENTAL_PRICE = "SELECT t.ID,t.SHOP_CODE,t.GOODS_CODE,SALE_PRICE,MEMBER_PRICE FROM SHOP_GOODS_PRICE_LAST t " +
            " WHERE EXISTS(SELECT 1 FROM PLATFORM_SHOP s WHERE t.SHOP_CODE = s.SJ_SHOP_CODE AND STATUS = 1 AND s.PLATFORM_ID=10002 ) " +
            " AND UPDATE_DATE >= ?";

    /**
     * 最近更新的价格数据
     *
     * @return 最近更新的价格数据列表
     */
    protected List<LastGoodsPrice> getIncrementalGoodsPrice(LocalDateTime syncTime) {
        Timestamp timestamp = lastSyncPriceTiemOperations.get(RedisConstant.LAST_PRICE_SYNCJDDJ_TIME);
        if (null == timestamp) {
            timestamp = Timestamp.valueOf(syncTime.toLocalDate().atStartOfDay());
        }

        List<LastGoodsPrice> lastGoodsPriceList = jdbcTemplate.query(SQL_QUERY_INCREMENTAL_PRICE, new Object[]{timestamp}, getIncrementalRowMapper());

        return lastGoodsPriceList;
    }


    /**
     * 配置rowmapper
     */
    private RowMapper<LastGoodsPrice> getIncrementalRowMapper() {
        return (resultSet, i) -> {
            LastGoodsPrice goodsPrice = new LastGoodsPrice();
            goodsPrice.setShopCode(resultSet.getString("SHOP_CODE"));
            goodsPrice.setGoodsCode(resultSet.getString("GOODS_CODE"));
            goodsPrice.setSalePrice(resultSet.getLong("SALE_PRICE"));
            return goodsPrice;
        };
    }

    private static final String UPDATE_PRICE_CHANGE_SQL = "Update GOODS_PRICE_SYNC Set  BATCH_SALE_PRICE=?, BATCH_MARKET_PRICE=?,SYNC_TIME=?  WHERE STORE_ID=? and GOODS_ID=?";


    public int UpdateGoodsPrice(String storeId, String goodsId, BigDecimal price, BigDecimal market, Date syncTime) {
        return jingDongDaoJiaJdbcTemplate.update(UPDATE_PRICE_CHANGE_SQL, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setBigDecimal(1, price);
                ps.setBigDecimal(2, market);
                ps.setTimestamp(3, new Timestamp(syncTime.getTime()));
                ps.setString(4, storeId);
                ps.setString(5, goodsId);
            }
        });

    }


    private static final String UPDATE_STOCK_CHANGE_SQL = "update GOODS_STOCK_SYNC set STORE_STOCK=?, CURRENT_STOCK=?, GOODS_STATUS=?, SYNC_TIME=sysdate " +
            "      where STORE_ID=? and GOODS_ID=? ";

    public int UpdateGoodsStockSync(String storeId, String goodsId, int stockNum, int validStock, int status) {
        return jingDongDaoJiaJdbcTemplate.update(UPDATE_STOCK_CHANGE_SQL, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, stockNum);
                ps.setInt(2, validStock);
                ps.setInt(3, status);
                ps.setString(4, storeId);
                ps.setString(5, goodsId);
            }
        });
    }

}

