package com.example.domain.sjhub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by gaoqichao on 16-7-18.
 * 增量价格同步信息表
 */
@Data
@Entity(name = "SHOP_GOODS_PRICE_LAST")
public class LastGoodsPrice implements Serializable {
    @Id
    private String id;
    
    /**
     * 三江门店id
     */
    private String shopCode;
    
    /**
     * 商品编码
     */
    private String goodsCode;
    
    /**
     * 零售价
     */
    private Long salePrice;
    
    /**
     * 会员价
     */
    private Long memberPrice;
    
    /**
     * 更新时间
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime updateDate;
}
