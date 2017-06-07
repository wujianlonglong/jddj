package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/2/10.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDAfsResponseResult {
    /// <summary>
    /// 原订单号
    /// "orderId": "100001030844298",
    /// </summary>
    private BigDecimal orderId ;

    /// <summary>
    /// 售后单号
    /// "afsServiceOrder": "20016815",
    /// </summary>
    private BigDecimal afsServiceOrder ;

    /// <summary>
    /// 售后单状态（10:待审核,20:待取件,30:退款处理中,31:待商家收货审核,32:退款成功,33:退款失败,40:审核不通过-驳回,50:客户取消,60:商家收货审核不通过,70:已解决,80:待补货,81:补货,82:补货成功,83:补货失败,91:直赔,92:直赔成功,93:直赔失败,90:待赔付,100:待换新,1001:换新中,101:送货成功,102:换新失败,103:换新成功,110:待退货,111:取货成功,112:退货成功-待退款,113:退货失败,114:退货成功）
    /// "afsServiceState": 32,
    /// </summary>
    private int afsServiceState ;

    /// <summary>
    /// 审核时间
    /// 
    /// </summary>
    private JDTimeObj approvedDate ;

    /// <summary>
    /// 审核人
    /// "approvePin": "changmi3",
    /// </summary>
    private String approvePin ;

    /// <summary>
    /// 申请售后原因（201:产品质量,202:发错货,203:少件,204:临期,205:假货/二手货,206:产品误导,207:外观原因,301:物流损,302:提前妥投,401:误购,402:7天无理由,501:未收到商品,502:超期配送,601:其他）
    /// "questionTypeCid": 201,
    /// </summary>
    private int questionTypeCid ;

    /// <summary>
    /// 问题描述
    /// "questionDesc": "您好,100001030844298，客户反映(土豆和茄子都烂的)，建议申请退款，告知但是这个退款的话，是会扣除优惠券的比例，客户接受，谢谢",
    /// </summary>
    private String questionDesc ;

    /// <summary>
    /// 下单顾客账号（到家账号）
    /// "customerPin": "JD_2b7w3081b2ab1",
    /// </summary>
    private String customerPin ;

    /// <summary>
    /// 取货信息（发错货时填写的取货信息）
    /// "pickupDetail": "",
    /// </summary>
    private String pickupDetail ;

    /// <summary>
    /// 客户姓名
    /// "customerName": "黄艳平",
    /// </summary>
    private String customerName ;

    /// <summary>
    /// 客户电话
    /// "customerMobilePhone": "13566016860",
    /// </summary>
    private String customerMobilePhone ;

    /// <summary>
    /// 客户地址
    /// "pickwareAddress": "宁波市海曙区汪弄社区筱墙巷79号305",
    /// </summary>
    private String pickwareAddress ;

    /// <summary>
    /// 承运商（9966：众包配送，2938：商家自送）
    /// "carriersNo": "9966",
    /// </summary>
    private String carriersNo ;

    /// <summary>
    /// 运单号（承运商为众包时的退货单才有；承运商为商家自送时没有）
    /// "deliveryNo": null,
    /// </summary>
    private String deliveryNo ;

    /// <summary>
    /// 商家门店编号
    /// "stationNumOutSystem":"	10342",
    /// </summary>
    private String stationNumOutSystem ;


    /// <summary>
    /// 门店编号（到家的门店号）
    /// "stationId": "10034584",
    /// </summary>
    private String stationId ;

    /// <summary>
    /// 门店名称
    /// "stationName": "三江购物-胜丰店",
    /// </summary>
    private String stationName ;

    /// <summary>
    /// 预计送达时间开始
    /// 
    /// </summary>
    private JDTimeObj pickupStartTime ;

    /// <summary>
    /// 预计送达时间结束
    /// 
    /// </summary>
    private JDTimeObj pickupEndTime ;

    /// <summary>
    /// 订单时效（分钟）
    /// "orderAging": 120,
    /// </summary>
    private int orderAging ;

    /// <summary>
    /// 该售后单实际退用户支付金额（单位为分，实际用户支付金额=售后单总金额-京豆金额-优惠总金额）
    /// "cashMoney": 820,
    /// </summary>
    private long cashMoney ;

    /// <summary>
    /// 原订单支付方式（1:货到付款,2:邮局汇款,3:混合支付,4:在线支付,5:公司转账）
    /// "payType": 1,
    /// </summary>
    private int payType ;

    /// <summary>
    /// 售后单应退款金额（单位为分，售后单原始总金额-优惠总金额）
    /// "afsMoney": 820,
    /// </summary>
    private long afsMoney ;

    /// <summary>
    /// 运费金额（单位为分，售后单运费金额为0）
    /// "orderFreightMoney": 0,
    /// </summary>
    private long orderFreightMoney ;

    /// <summary>
    /// 售后单实际退用户京豆支付金额（单位为分）
    /// "jdBeansMoney": 0,
    /// </summary>
    private long jdBeansMoney ;

    /// <summary>
    /// 售后单总优惠金额（单位为分，售后单维度的总优惠金额=SKU维度总优惠金额相加）
    /// "virtualMoney": 0,
    /// </summary>
    private long virtualMoney ;

    /// <summary>
    /// 售后单类型（10:仅退款,20:补货,30:直赔，40:退货退款，50:上门换新）
    /// "applyDeal": "10",
    /// </summary>
    private String applyDeal ;

    /// <summary>
    /// 责任承担方（1:京东到家,2: 商家,3:物流,4:客户,5:其他）
    /// "dutyAssume": 2,
    /// </summary>
    private int dutyAssume ;

    /// <summary>
    /// 原订单状态，指原订单申请售后时的状态
    /// "orderStatus": 90000,
    /// </summary>
    private int orderStatus ;

    /// <summary>
    /// 运单状态，指众包配送的退货单的运单状态
    /// "deliveryState": null,
    /// </summary>
    private String deliveryState ;

    /// <summary>
    /// 配送员姓名，商家自送时没有
    /// "deliveryMan": null,
    /// </summary>
    private String deliveryMan ;

    /// <summary>
    /// 配送员电话，商家自送时没有
    /// "deliveryMobile": null,
    /// </summary>
    private String deliveryMobile ;

    /// <summary>
    /// 配送员编号，商家自送时没有
    /// "deliveryManNo": null,
    /// </summary>
    private String deliveryManNo ;

    /// <summary>
    /// 原订单类型 （10000:从门店出的订单,10010:众筹订单,20000:服务订单,30000:美食订单,40000:厂商直送类型,50000:一般自营类订单,60000:开放仓类订单）
    /// "orderType": 10000,
    /// </summary>
    private int orderType ;

    /// <summary>
    /// 
    /// 
    /// </summary>
    private List<JDAfsResponseAfsDetail> afsDetailList ;

    /// <summary>
    /// 创建时间
    /// 
    /// </summary>
    //private String creattime ;

    /// <summary>
    /// 更新时间
    /// 
    /// </summary>
    //private String updatetime ;

    public  JDAfsResponseResult()
    {
        afsDetailList = new ArrayList<>();
    }
}
