package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDOrderQueryResponseData {

    private String code ;

    private String msg ;

    private Boolean success ;

    private JDOrderQueryResult result ;

    public JDOrderQueryResponseData()
    {
        result = new JDOrderQueryResult();
    }
}
