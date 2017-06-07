package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/1/26.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderInfoQueryResponseData {

    private String code ;

    private String  msg ;

    private boolean success ;

    public JDOrderInfoQueryResponseResult result ;

    public JDOrderInfoQueryResponseData()
    {
        result = new JDOrderInfoQueryResponseResult();
    }
}
