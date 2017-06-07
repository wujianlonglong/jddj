package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wujianlong on 2016/10/27.
 */

@Data
public class DtJDBatchSyncPriceRequest
{

    private List<JDGoodsPriceItemObj> goodsItemList = new ArrayList<JDGoodsPriceItemObj>();



}