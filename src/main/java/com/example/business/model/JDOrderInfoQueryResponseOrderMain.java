package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/26.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderInfoQueryResponseOrderMain {
    /// <summary>
    /// 订单号
    /// </summary>
    private BigDecimal orderId ;

    /// <summary>
    /// 来源系统id,详细见码表
    /// </summary>
    private int srcSysId ;

    /// <summary>
    /// 来源订单号
    /// </summary>
    private String srcOrderId ;

    /// <summary>
    /// 来源平台id
    /// </summary>
    private String srcPlatId ;

    /// <summary>
    /// 来源订单类型
    /// </summary>
    private int srcOrderType ;

    /// <summary>
    /// 内部订单来源类型(0:原订单，1：换货单，2：拆单...)
    /// </summary>
    private int srcInnerType ;

    /// <summary>
    /// 内部订单来源订单号
    /// </summary>
    private String srcInnerOrderId ;

    /// <summary>
    /// 10000 从门店出的订单 20000 服务订单 90000 临时美食订单（临时类型）
    /// </summary>
    private int orderType ;

    /// <summary>
    /// 京东订单标识码（京东订单直接记录，其他类型订单值为空）此字段暂不启用
    /// </summary>
    private String orderJdSendpay ;

    /// <summary>
    /// 订单业务处理唯一标识(默认50个0)
    /// </summary>
    private String orderBizUuid ;

    /// <summary>
    /// 订单库存归属（生鲜、冷藏、冷冻、发码）拆单拣货用(3 未维护)
    /// </summary>
    private int orderStockOwner ;

    /// <summary>
    /// 订单商品类型(0:其他,1:鲜食)
    /// </summary>
    private int orderSkuType ;

    /// <summary>
    /// 订单状态
    /// </summary>
    private int orderStatus ;

    /// <summary>
    /// 订单状态最新更改时间
    /// </summary>
    private JDTimeObj orderStatusTime ;

    /// <summary>
    /// 下单时间
    /// </summary>
    private JDTimeObj orderStartTime ;

    /// <summary>
    /// 订单成交时间(货到付款类型订单的下单时间 or 在线支付类型订单的付款完成时间)
    /// </summary>
    private JDTimeObj orderPurchaseTime ;

    /// <summary>
    /// 订单时效类型
    /// </summary>
    private int orderAgingType ;

    /// <summary>
    /// 订单预计送达时间(此字段暂不启用)
    /// </summary>
    private JDTimeObj orderPreDeliveryTime ;

    /// <summary>
    /// 预计送达开始时间
    /// </summary>
    private JDTimeObj orderPreStartDeliveryTime ;

    /// <summary>
    /// 预计结束送达时间
    /// </summary>
    private JDTimeObj orderPreEndDeliveryTime ;

    /// <summary>
    /// 订单取消时间
    /// </summary>
    private Date orderCancelTime ;

    /// <summary>
    /// 订单取消备注
    /// </summary>
    private String orderCancelRemark ;

    /// <summary>
    /// 订单删除时间
    /// </summary>
    private JDTimeObj orderDeleteTime ;

    /// <summary>
    /// 订单是否关闭
    /// </summary>
    private Boolean orderIsClosed ;

    /// <summary>
    /// 订单关闭时间
    /// </summary>
    private JDTimeObj orderCloseTime ;

    /// <summary>
    /// 组织编号(商家在O2O平台的组织编号)
    /// </summary>
    private String orgCode ;

    /// <summary>
    /// pop商家编号(京东商家管理平台中，商家唯一编号)
    /// </summary>
    private String popVenderId ;

    /// <summary>
    /// 买家账号类型
    /// </summary>
    private int buyerPinType ;

    /// <summary>
    /// 买家账号
    /// </summary>
    private String buyerPin ;

    /// <summary>
    /// 买家昵称
    /// </summary>
    private String buyerNickName ;

    /// <summary>
    /// buyerFullName
    /// </summary>
    private String buyerFullName ;

    /// <summary>
    /// 收货人详细地址
    /// </summary>
    private String buyerFullAddress ;

    /// <summary>
    /// 收货人电话
    /// </summary>
    private String buyerTelephone ;

    /// <summary>
    /// 收货人手机号
    /// </summary>
    private String buyerMobile ;

    /// <summary>
    /// 省Id
    /// </summary>
    private String buyerProvince ;

    /// <summary>
    /// 市Id
    /// </summary>
    private String buyerCity ;

    /// <summary>
    /// 县Id
    /// </summary>
    private String buyerCountry ;

    /// <summary>
    /// 镇Id
    /// </summary>
    private String buyerTown ;

    /// <summary>
    /// 生产门店编号
    /// </summary>
    private String produceStationNo ;

    /// <summary>
    /// 生产门店名称
    /// </summary>
    private String produceStationName ;

    /// <summary>
    /// 外部生产门店编号
    /// </summary>
    private String produceStationNoIsv ;

    /// <summary>
    /// 配送门店编号
    /// </summary>
    private String deliveryStationNo ;

    /// <summary>
    /// 配送门店名称
    /// </summary>
    private String deliveryStationName ;

    /// <summary>
    /// 外部配送门店编号
    /// </summary>
    private String deliveryStationNoIsv ;

    /// <summary>
    /// 配送类型(1京东配送 ，2：商家门店自送 3 第三方配送 4 自提)
    /// </summary>
    private int deliveryType ;

    /// <summary>
    /// 承运商编号
    /// </summary>
    private String deliveryCarrierNo ;

    /// <summary>
    /// 承运商名称
    /// </summary>
    private String deliveryCarrierName ;

    /// <summary>
    /// 承运单号
    /// </summary>
    private String deliveryBillNo ;

    /// <summary>
    /// 包裹重量
    /// </summary>
    private BigDecimal deliveryPackageWeight ;

    /// <summary>
    /// 包裹体积
    /// </summary>
    private BigDecimal deliveryPackageVolume ;

    /// <summary>
    /// 配送员编号
    /// </summary>
    private String deliveryManNo ;

    /// <summary>
    /// 配送员姓名
    /// </summary>
    private String deliveryManName ;

    /// <summary>
    /// 配送员电话
    /// </summary>
    private String deliveryManPhone ;

    /// <summary>
    /// 妥投时间
    /// </summary>
    private JDTimeObj deliveryConfirmTime ;

    /// <summary>
    /// 订单支付类型
    /// </summary>
    private int orderPayType ;

    /// <summary>
    /// 订单自提码
    /// </summary>
    private String orderTakeSelfCode ;

    /// <summary>
    /// 订单总金额，商品总金额
    /// </summary>
    private int orderTotalMoney ;

    /// <summary>
    /// 订单优惠总金额
    /// </summary>
    private int orderDiscountMoney ;

    /// <summary>
    /// 订单运费总金额
    /// </summary>
    private int orderFreightMoney ;

    /// <summary>
    /// 订单货款总金额(此字段暂不启用)
    /// </summary>
    private Integer orderGoodsMoney ;

    /// <summary>
    /// 用户应付金额
    /// </summary>
    private int orderBuyerPayableMoney ;

    /// <summary>
    /// 商家再收金额
    /// </summary>
    private int orderVenderChargeMoney ;

    /// <summary>
    /// 包装金额
    /// </summary>
    private int packagingMoney ;

    /// <summary>
    /// 订单使用余额
    /// </summary>
    private int orderBalanceUsed ;

    /// <summary>
    /// 订单开发票标识
    /// </summary>
    private int orderInvoiceOpenMark ;

    /// <summary>
    /// 订单结算财务机构号
    /// </summary>
    private String orderFinanceOrgCode ;

    /// <summary>
    /// 是否京东收款
    /// </summary>
    @JsonProperty("isJdGetcash")
    private Boolean isJdGetcash ;

    /// <summary>
    /// 是否存在调整单
    /// </summary>
    private Boolean adjustIsExists ;

    /// <summary>
    /// 调整次数记录
    /// </summary>
    private int adjustCount ;

    /// <summary>
    /// 最新确认单id
    /// </summary>
    private BigDecimal adjustId ;

    private JDTimeObj ts ;

    private int orderJingdouMoney ;

    private String serviceManName ;

    private String serviceManPhone ;

    private JDOrderQueryOrderExtend orderExtend ;

    private List<JDOrderQueryOrderProduct> orderProductList ;

    private List<JDOrderInfoQueryOrderDiscount> orderDiscountList ;

    public JDOrderInfoQueryResponseOrderMain()
    {
        orderId = BigDecimal.ZERO;

        orderJdSendpay = "";//此字段暂不启用
        orderPreDeliveryTime = null;//此字段暂不启用
        deliveryManNo = "";
        deliveryManName = "";
        deliveryManPhone = "";

        orderGoodsMoney = null;

        orderExtend = new JDOrderQueryOrderExtend();
        orderProductList = new ArrayList<>();
        orderDiscountList = new ArrayList<>();
    }
}
