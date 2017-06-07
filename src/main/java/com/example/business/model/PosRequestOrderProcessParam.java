package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/10.
 */


@Data
public class PosRequestOrderProcessParam
{

    private OperatorParam Operator ;


    private PosParam Pos ;


    private String OrderId;


    public PosRequestOrderProcessParam()
    {
        Operator = new OperatorParam();
        Pos = new PosParam();
        OrderId = "";
    }
}

