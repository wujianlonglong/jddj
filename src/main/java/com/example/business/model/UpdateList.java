package com.example.business.model;

import com.example.domain.sjhub.StockSync;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/3.
 */
@Data
public class UpdateList {

    List<JDGoodsStockItemObj> goodsItemList;

    List<JDBatchStockSync> updateHistoryList;

    List<StockSync> stockSyncList;

    public UpdateList(){
        goodsItemList=new ArrayList<JDGoodsStockItemObj>();
        updateHistoryList=new ArrayList<JDBatchStockSync>();
        stockSyncList=new ArrayList<StockSync>();

    }
}
