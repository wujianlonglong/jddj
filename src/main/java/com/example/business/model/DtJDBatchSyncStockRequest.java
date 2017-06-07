package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2016/12/7.
 */
@Data
public class DtJDBatchSyncStockRequest {
    private List<JDGoodsStockItemObj> goodsItemList ;

    public DtJDBatchSyncStockRequest()
    {
        goodsItemList = new ArrayList<JDGoodsStockItemObj>();
    }
}
