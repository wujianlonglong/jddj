package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/26.
 */
@Data
public class JDTimeObj {
    private int year ;

    private int month ;

    private int date ;

    private int hours ;

    private int minutes ;

    private int seconds ;

    private int day ;

    private long time ;

    private int timezoneOffset ;

    public JDTimeObj()
    {

    }
}
