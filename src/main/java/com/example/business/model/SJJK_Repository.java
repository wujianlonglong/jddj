package com.example.business.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by shilhu on 2016/10/28.
 */
@Repository
public class SJJK_Repository {

    @Autowired
    protected JdbcTemplate stockJdbcTemplate;


    public List<JingDongStorePO> getJinDongShopList() {
         String JINGDONG_SHOP_LIST_SQL = "SELECT BMBH,IP FROM DM_SCBH WHERE BMBH IN (SELECT SCBH FROM JDDJ_SCBH) ORDER BY BMBH ";
         return stockJdbcTemplate.query(JINGDONG_SHOP_LIST_SQL,getRowMapper());
    }

    /**
     * 配置rowmapper
     */
    private RowMapper<JingDongStorePO> getRowMapper() {
        return (resultSet, i) -> {
            JingDongStorePO jingDongStorePO = new JingDongStorePO();
            jingDongStorePO.setStoreId(resultSet.getString("BMBH"));
            jingDongStorePO.setStoreIP(resultSet.getString("IP"));
            return jingDongStorePO;
        };
    }

    public List<BigDecimal>  getMarkPrice(String shopid,String  goodsid) {
        String JINGDONG_SHOP_LIST_SQL = "select normalprice from GOODSSHOP@CSHQ where shopid=? and goodsid=?";
        List<BigDecimal> marketPriceList =  stockJdbcTemplate.query(JINGDONG_SHOP_LIST_SQL,new Object[]{shopid,goodsid},getMarketPriceRowMapper());

        return marketPriceList;
    }

    /**
     * 配置rowmapper
     */
    private RowMapper<BigDecimal> getMarketPriceRowMapper() {
        return (resultSet, i) -> {
            return resultSet.getBigDecimal("normalprice");
        };
    }


}
