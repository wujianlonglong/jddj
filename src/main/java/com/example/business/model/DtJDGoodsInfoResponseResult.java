package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/6/15.
 */
@Data
public class DtJDGoodsInfoResponseResult {
    private int count ;

    private List<DtJDGoodsInfoResponseGoods> result ;

    public DtJDGoodsInfoResponseResult()
    {
        count = 0;
        result = new ArrayList<>();
    }
}
