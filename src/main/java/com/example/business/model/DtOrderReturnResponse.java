package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/2/22.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtOrderReturnResponse
{
    private String code ;

    private String msg ;

    private String detail ;

    private String success ;

    public DtOrderReturnResponse()
    {
        code = "";
        msg = "";
        detail = "";
        success="";
    }
}
