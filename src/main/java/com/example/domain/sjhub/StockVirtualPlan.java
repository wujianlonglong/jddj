package com.example.domain.sjhub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by gaoqichao on 16-7-20.
 */
@Data
@Entity(name = "STOCK_VIRTUAL_PLAN")
public class StockVirtualPlan {
    @Id
    private String id;
    
    /**
     * 计划编号
     */
    private String planId;
    
    /**
     * 计划名称
     */
    private String planName;
    
    /**
     * 虚拟库存更新时间
     */
    private String virSyncTime;
    
    /**
     * 锁定库存更新时间
     */
    private String virLockSyncTime;
    
    /**
     * 虚拟库存更新同步状态
     */
    private Integer virStatus;
    
    /**
     * 锁定库存更新同步状态
     */
    private Integer virLockStatus;
    
    /**
     * 三江门店编号
     */
    private String sjShopCode;
    
    /**
     * 三江管理编码
     */
    private String sjGoodsCode;
    
    /**
     * 虚拟库存预设数量
     */
    private Integer virNum;
    
    public int getVirNum() {
        return this.virNum == null ? 0 : virNum;
    }
    
    /**
     * 创建时间
     */
    @JsonIgnore
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime createTime;
    
    /**
     * 创建人
     */
    @JsonIgnore
    private Long createBy;

    /**
     * 是否有效
     */
    @JsonIgnore
    private Integer goodsStatus;


}
