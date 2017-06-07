package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/6/5.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDChangePriceResponseObj extends JDSysResponseObj {
    private DtChangePriceResponse data;

    public JDChangePriceResponseObj() {
        data = new DtChangePriceResponse();
    }
}
