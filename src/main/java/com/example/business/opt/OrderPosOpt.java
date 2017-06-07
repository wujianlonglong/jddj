package com.example.business.opt;

/**
 * Created by wujianlong on 2017/3/23.
 */

import com.example.business.model.PosRequestOrderProcessParam;
import com.example.business.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

@Service
public class OrderPosOpt {

    @Autowired
    OrderService orderService;

    public String orderSearch(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        return orderService.orderSearch(requestparam, servletRequest);
    }

    public String OrderPos(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        return orderService.OrderPos(requestparam, servletRequest);
    }

    public String returnSercher(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException {
        return orderService.returnSercher(requestparam, servletRequest);
    }

    public String returnConfirm(PosRequestOrderProcessParam requestparam, HttpServletRequest servletRequest) throws IOException, SQLException {
        return orderService.returnConfirm(requestparam, servletRequest);
    }



}
