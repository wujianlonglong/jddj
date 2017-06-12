package com.example.domain.sjhub;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kimiyu on 15/12/11.
 */
@Data
@Entity
@Table(name = "XT_KCB")
public class XtStore implements Serializable {
    
    @Id
    @Column(name = "SCBH")
    private String sjShopCode;
    
    /**
     * 管理编号
     */
    @Column(name = "GLBH")
    private String sjGoodsCode;
    
    /**
     * 库存数量
     */
    @Column(name = "KCSL")
    private int stockNumber;

    @Transient
    private int status;
}
