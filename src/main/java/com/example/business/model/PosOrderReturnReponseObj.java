package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/2/20.
 */
@Data
public class PosOrderReturnReponseObj extends JsExcuteStatus {

    private PosOrderReturnReponseData Data ;

    public PosOrderReturnReponseObj()
    {
        Data = new PosOrderReturnReponseData();
    }
}
