package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/6/10.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtSingleStockUpdateResponse {
    public String retCode ;

    public String retMsg ;

    public String isRet ;
}
