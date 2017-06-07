package com.example.utils.enums;

/**
 * Created by gaoqichao on 16-7-7.
 */
public enum BaiduOrderStatusEnum {
    /**
     * 等待确认
     */
    CREATED(1, "等待确认"),
    /**
     * 已确认
     */
    CONFIRMED(5, "已确认"),
    /**
     * 正在取餐
     */
    TAKING(7, "正在取餐"),
    /**
     * 正在配送
     */
    DELIVERING(8, "正在配送"),
    /**
     * 已完成
     */
    FINISHED(9, "已完成"),
    /**
     * 已取消
     */
    CANCELED(10, "已取消"),

    /**
     * 未知状态
     */
    UNKNOWN(-1, "未知状态");

    BaiduOrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 订单状态码
     */
    private int code;

    /**
     * 订单状态描述
     */
    private String desc;

    public int code() {
        return code;
    }

    public String desc() {
        return desc;
    }

    /**
     * 根据订单状态码取得对应的订单状态枚举值
     *
     * @param code 订单状态码
     * @return 订单状态枚举值
     */
    public static BaiduOrderStatusEnum of(int code) {
        BaiduOrderStatusEnum statusEnum = UNKNOWN;
        switch (code) {
            case 1:
                statusEnum = CREATED;
                break;
            case 5:
                statusEnum = CONFIRMED;
                break;
            case 7:
                statusEnum = TAKING;
                break;
            case 8:
                statusEnum = DELIVERING;
                break;
            case 9:
                statusEnum = FINISHED;
                break;
            case 10:
                statusEnum = CANCELED;
                break;
            default:
                break;

        }
        return statusEnum;
    }
}
