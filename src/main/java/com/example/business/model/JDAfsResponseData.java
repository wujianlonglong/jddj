package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/2/10.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDAfsResponseData {
    private String code ;

    private String msg ;

    private Boolean success ;

    private JDAfsResponseResult result ;

    public JDAfsResponseData()
    {
        result = new JDAfsResponseResult();
    }
}
