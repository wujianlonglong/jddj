package com.example.business.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wujianlong on 2016/10/31.
 */
@Repository
public class SJHUB_Repository {

    @Autowired
    protected JdbcTemplate jdbcTemplate;


    public List<SjhubPricePO> getSjhubPrice(String shopid) {
        String SJHUB_PRICE_LIST_SQL = "select a.glbh, a.scbh, a.jzsj,a.jzhyj,b.sj_goods_code from XT_LWXX a,PRICE_IMPORT b where  a.scbh=b.sj_shop_code(+) and a.glbh=b.sj_goods_code(+) and  a.glbh in ( select glbh from JDDJ_GOODS_SYNC@DBL_SJJK ) and a.scbh="+shopid;
        return jdbcTemplate.query(SJHUB_PRICE_LIST_SQL,getRowMapper());
    }

    /**
     * 配置rowmapper
     */
    private RowMapper<SjhubPricePO> getRowMapper() {
        return (resultSet, i) -> {
            SjhubPricePO sjhubPricePO = new SjhubPricePO();
            sjhubPricePO.setGlbh(resultSet.getString("glbh"));
            sjhubPricePO.setScbh(resultSet.getString("scbh"));
            sjhubPricePO.setSj_goods_code(resultSet.getString("sj_goods_code"));
            sjhubPricePO.setJzsj(resultSet.getBigDecimal("jzsj"));
            sjhubPricePO.setJzhyj(resultSet.getBigDecimal("jzhyj"));
            return sjhubPricePO;
        };
    }

}
