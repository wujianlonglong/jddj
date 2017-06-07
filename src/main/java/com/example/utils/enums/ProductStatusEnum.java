package com.example.utils.enums;

/**
 * Created by gaoqichao on 16-7-15.
 */
public enum ProductStatusEnum {
    /**
     * 删除
     */
    DELETED(0),

    /**
     * 上架
     */
    ON_SHELF(1),

    /**
     * 下架
     */
    OFF_SHELF(2),

    /**
     * 其他状态
     */
    UNKNOWN(-100);

    ProductStatusEnum(int status) {
        this.status = status;
    }

    private int status;

    public int status() {
        return this.status;
    }

    public static ProductStatusEnum of(int status) {
        ProductStatusEnum statusEnum = UNKNOWN;

        switch (status) {
            case 1:
                statusEnum = ON_SHELF;
                break;
            case 2:
                statusEnum = OFF_SHELF;
                break;
            case 0:
                statusEnum = DELETED;
                break;
            default:
                break;
        }
        return statusEnum;
    }
}
