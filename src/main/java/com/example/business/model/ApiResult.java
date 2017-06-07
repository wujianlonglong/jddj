package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/6/5.
 */
@Data
public class ApiResult {
    private boolean success;

    private String code;

    private String msg;

    public ApiResult(){
        success=false;
        code="-1";
    }
}
