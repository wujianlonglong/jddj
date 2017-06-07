package com.example.domain.sjhub;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by gaoqichao on 16-7-5.
 */
@Data
@Entity(name = "PLATFORM_SHOP")
public class PlatformShop implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shopGenerator")
    @SequenceGenerator(name = "shopGenerator", sequenceName = "outpay_shop_sequence", allocationSize = 1)
    private Long id;

    /**
     * 三江门店编码
     */
    private String sjShopCode;

    /**
     * 三江门店名称
     */
    private String sjShopName;

    /**
     * 平台编码
     */
    private String platformId;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 平台门店编码
     */
    private String platformShopCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
}
