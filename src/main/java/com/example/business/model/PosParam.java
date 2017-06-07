package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/10.
 */


@Data
public class PosParam
{
    private String StoreId ;


    private String PosNo ;


    private String PosStream ;


    private String posTime ;

    public PosParam()
    {
        StoreId="";
        PosNo = "";
        PosStream = "";
        posTime = "";
    }
}

