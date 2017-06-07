package com.example.controller;

import com.example.business.model.PosRequestOrderProcessParam;
import com.example.business.opt.OrderPosOpt;
import com.example.repository.jddj.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by wujianlong on 2017/3/23.
 */

@RestController
@RequestMapping("/jdorderpos")
public class OrderPosController {

    @Autowired
    OrderPosOpt orderPosOpt;

    @Autowired
    TestRepository testRepository;


    /**
     * 取得订单详情信息
     *
     * @param requestparam   查询订单请求信息
     * @param servletRequest http请求
     * @return 查询订单响应结果
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ordersearch")
    public String orderSearch(@RequestBody PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        return orderPosOpt.orderSearch(requestparam, servletRequest);

    }


    /**
     * 服务台过机
     *
     * @param requestparam   过机请求信息
     * @param servletRequest http请求
     * @return 过机处理结果
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/orderpos")
    public String orderPos(@RequestBody PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        return orderPosOpt.OrderPos(requestparam, servletRequest);
    }


    /**
     * 取得退货订单详情
     *
     * @param requestparam   退货请求信息
     * @param servletRequest http请求
     * @return 查询退货订单响应结果
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/returnsercher")
    public String returnSercher(@RequestBody PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        return orderPosOpt.returnSercher(requestparam, servletRequest);
    }


    /**
     * 服务台退货
     *
     * @param requestparam   退货请求信息
     * @param servletRequest http请求
     * @return 退货处理结果
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/returnconfirm")
    public String returnConfirm(@RequestBody PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException, SQLException {
        return orderPosOpt.returnConfirm(requestparam, servletRequest);
    }

}
