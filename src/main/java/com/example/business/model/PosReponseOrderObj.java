package com.example.business.model;

import lombok.Data;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class PosReponseOrderObj extends JsExcuteStatus {
    private DtPosReponseOrder Data ;

    public PosReponseOrderObj()
    {
        Data = new DtPosReponseOrder();
    }

}
