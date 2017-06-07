package com.example.business.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by wujianlong on 2017/3/14.
 */
@Data
public class OrderProcess {

    private PosRequestOrderProcessParam request;
    private int processType;
    private Date requestTime;
    private String requestIp;


    public OrderProcess(PosRequestOrderProcessParam requestp, int processTypep, Date requestTimep, String requestIpp) {
        request = requestp;
        processType = processTypep;
        requestTime = requestTimep;
        requestIp = requestIpp;
    }



}
