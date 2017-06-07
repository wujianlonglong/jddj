package com.example.domain.sjhub;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Created by 如水放逐 on 2016/8/31.
 */
@Data
@Entity(name = "PRICE_IMPORT")
public class PriceImport {
    @Id
    private String id;
    
    /**
     * 三江门店编码
     */
    private String sjShopCode;
    
    /**
     * 商品编码
     */
    private String sjGoodsCode;
    
    /**
     * 商品零售价
     */
    private long retailPrice;
    
    /**
     * 操作人
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime = LocalDateTime.now();
}
