package com.example.business.model;

import lombok.Data;

import java.util.Date;

/**
 * Created by wujianlong on 2017/3/15.
 */
@Data
public class AfsProcess {

    private PosRequestOrderProcessParam request;
    private Date dealtime;
    private String Ip;

    public  AfsProcess(PosRequestOrderProcessParam requstpar, Date dealtimepar, String Ippar) {
        request = requstpar;
        dealtime = dealtimepar;
        Ip = Ippar;
    }

}
