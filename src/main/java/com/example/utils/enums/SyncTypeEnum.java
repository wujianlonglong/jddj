package com.example.utils.enums;

/**
 * Created by gaoqichao on 16-7-22.
 */
public enum SyncTypeEnum {
    /**
     * 库存同步
     */
    STOCK(1, "库存同步"),
    /**
     * 价格同步
     */
    PRICE(2, "价格同步");


    SyncTypeEnum(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    private int type;

    private String typeName;

    public int type() {
        return this.type;
    }

    public String typeName() {
        return this.typeName;
    }
}
