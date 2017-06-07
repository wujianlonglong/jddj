package com.example.business.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by wujianlong on 2017/4/1.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JdBatchnoQueryResponse extends JDSysResponseObj{
    private JdBatchnoQueryResponseData data ;

    public JdBatchnoQueryResponse()
    {
        data = new JdBatchnoQueryResponseData();
    }
}
