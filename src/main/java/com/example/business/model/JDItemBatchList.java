package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2016/11/3.
 */
@Data
public class JDItemBatchList {
    private List<JDGoodsPriceItemObj> jDGoodsPriceItemList;
    private List<JDBatchPriceSync> jDGoodsPriceSyncHistoryList;

    public JDItemBatchList() {
        jDGoodsPriceItemList = new ArrayList<JDGoodsPriceItemObj>();
        jDGoodsPriceSyncHistoryList = new ArrayList<JDBatchPriceSync>();
    }

}
