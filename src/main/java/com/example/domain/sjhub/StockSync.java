package com.example.domain.sjhub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by gaoqichao on 16-7-20.
 */
@Data
@Entity(name = "STOCK_SYNC")
public class StockSync {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stockSyncGenetor")
    @SequenceGenerator(name = "stockSyncGenetor", sequenceName = "PLATFORM_STOCK_SEQ", allocationSize = 1)
    private Long id;
    
    /**
     * 门店编码
     */
    private String shopCode;
    
    /**
     * 商品编码
     */
    private String sjGoodsCode;
    
    /**
     * 上次同步时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastSyncTime;
    
    /**
     * 现货库存数量
     */
    private Integer stockNum;
    
    /**
     * 锁定库存时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lockTime;
    
    /**
     * 锁定库存数量
     */
    private Integer lockNum = 0;
    
    /**
     * 库存同步标志:0-不同步;1-同步
     */
    private int syncFlag;
    
    /**
     * 百度外卖预占库存
     */
    private int bdwmPreholdNum = 0;
    
    /**
     * 京东到家预占库存
     */
    private int jddjPreholdNum = 0;


    /**
     * 淘宝到家预占库存
     */
    private int tbdjPreholdNum = 0;

    /**
     * 三江网购预占库存
     */
    private int sjdsPreholdNum = 0;
}
