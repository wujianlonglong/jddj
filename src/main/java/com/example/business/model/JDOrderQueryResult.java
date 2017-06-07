package com.example.business.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianlong on 2017/1/11.
 */
@Data
public class JDOrderQueryResult {

    /// <summary>
    ///
    /// </summary>
    private int pageNo ;

    /// <summary>
    /// 每页数量
    /// </summary>
    private int pageSize ;

    private int maxPageSize;


    /// <summary>
    /// 总数量
    /// </summary>
    private int totalCount ;

    private int totalPage ;

    public int page ;

    /// <summary>
    /// 订单结果
    /// </summary>
    private List<JDOrderQueryOrderMain> resultList ;

    public JDOrderQueryResult()
    {
        resultList = new ArrayList<JDOrderQueryOrderMain>();
    }
}
