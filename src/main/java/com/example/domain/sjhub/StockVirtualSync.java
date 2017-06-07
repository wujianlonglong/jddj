package com.example.domain.sjhub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by wujianlong on 2017/3/30.
 */
@Data
@Entity(name = "STOCK_VIRTUAL_SYNC")
public class StockVirtualSync {
    @Id
    private String id;

    /**
     * 三江门店编号
     */
    private String sjShopCode;


    /**
     * 三江管理编码
     */
    private String sjGoodsCode;

    /**
     * 虚拟商品数量
     */
    private Integer virtualStockNum;


    /**
     * 创建时间
     */
    @JsonIgnore
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime updateTime;
}
