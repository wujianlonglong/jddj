package com.example.utils.enums;

/**
 * Created by gaoqichao on 16-6-22.
 */
public enum DiscountTypeEnum {
    /**
     * 立减优惠
     */
    IMMEDIATE_SUB("jian", "立减优惠"),

    /**
     * 免费配送
     */
    FREE_DELIVER("mian", "免费配送"),

    /**
     * 新用户立减
     */
    NEW_USER("xin", "新用户立减"),

    /**
     * 下单满赠
     */
    FULL_GIFT("zeng", "下单满赠"),

    /**
     * 在线支付营销
     */
    ONLINE_PAY("payenjoy", "在线支付营销"),

    /**
     * 预订优惠
     */
    PREORDER("preorder", "预订优惠"),

    /**
     * 返券优惠
     */
    REFUND_COUPON("fan", "返券优惠"),

    /**
     * 代金券优惠
     */
    COUPON("coupon", "代金券优惠"),

    /**
     * 未知优惠类型
     */
    UNKNOWN("unknown", "未知优惠类型");


    DiscountTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 优惠类型码
     */
    private String code;

    /**
     * 优惠类型说明
     */
    private String desc;

    public static DiscountTypeEnum of(String code) {
        DiscountTypeEnum discountTypeEnum = UNKNOWN;

        switch (code) {
            case "jian":
                discountTypeEnum = IMMEDIATE_SUB;
                break;
            case "mian":
                discountTypeEnum = FREE_DELIVER;
                break;
            case "xin":
                discountTypeEnum = NEW_USER;
                break;
            case "zeng":
                discountTypeEnum = FULL_GIFT;
                break;
            case "payenjoy":
                discountTypeEnum = ONLINE_PAY;
                break;
            case "preorder":
                discountTypeEnum = PREORDER;
                break;
            case "fan":
                discountTypeEnum = REFUND_COUPON;
                break;
            case "coupon":
                discountTypeEnum = COUPON;
                break;
            default:
                break;
        }
        return discountTypeEnum;
    }

    public String code() {
        return code;
    }

    public String desc() {
        return desc;
    }
}
