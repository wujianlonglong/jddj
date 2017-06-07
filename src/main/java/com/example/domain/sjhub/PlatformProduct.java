package com.example.domain.sjhub;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by gaoqichao on 16-7-15.
 */
@Data
@Entity(name = "PLATFORM_PRODUCT")
public class PlatformProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pordGenerator")
    @SequenceGenerator(name = "pordGenerator", sequenceName = "PLATFORM_PROD_ID_SEQ", allocationSize = 1)
    private Long id;

    /**
     * 平台id
     */
    private String platformId;

    /**
     * 三江商品id
     */
    private String sjGoodsCode;

    /**
     * 外部平台商品编码
     */
    private String platformGoodsCode;

    /**
     * 状态:0-删除;1-上架;2-下架
     */
    private Integer status = 0;

    /**
     * 特殊品标志:0-否:1-是
     */
    private Integer specFlag = 0;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createBy;
}
