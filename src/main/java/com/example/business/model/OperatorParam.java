package com.example.business.model;

import lombok.Data;


@Data
public class OperatorParam
{

    private String OperatorId ;

    private String OperatorName ;


    private String IP ;

    public OperatorParam()
    {
        OperatorId = "";
        OperatorName = "";
        IP = "";
    }
}
