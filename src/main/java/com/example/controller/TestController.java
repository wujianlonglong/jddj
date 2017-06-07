package com.example.controller;

import com.example.business.CommonJingdong.Common;
import com.example.business.JingdongUtil;
import com.example.business.model.JdBatchnoQueryResponse;
import com.example.business.service.OrderService;
import com.example.domain.sjhub.StockVirtualSync;
import com.example.domain.sjhub.TestTt;
import com.example.repository.sjhub.StockVirtualSyncRepository;
import com.example.repository.sjhub.TestttRepository;
import com.example.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by wujianlong on 2017/4/7.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    Common common;

    @Autowired
    StockVirtualSyncRepository stockVirtualSyncRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    TestttRepository testttRepository;

    /**
     * 测试京东到家流水号查询接口
     *
     * @param batchNo 流水号
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/batchnotest")
    public void test(@RequestParam(required = false, value = "batchNo") String batchNo) throws UnsupportedEncodingException {
        String str = common.queryBatch(batchNo);
        JdBatchnoQueryResponse jdBatchnoQueryResponse = JsonUtil.jsonToObject(str, JdBatchnoQueryResponse.class);
    }


    /**
     * 测试调用清空promotion的 redis缓存数据接口
     */
    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        Object object = restTemplate.getForObject("http://localhost:9999/jdredis/refreshprommotion", Object.class);
        String result = object.toString();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/virtest")
    public void virtesst() {
        List<StockVirtualSync> stockVirtualSyncList = stockVirtualSyncRepository.getValidVirStock();

    }


    @RequestMapping(method = RequestMethod.GET, value = "/jpatest")
    public void jpatest(@RequestParam(name = "key", required = false) String key, @RequestParam(name = "val", required = false) String val) throws SQLException, ClassNotFoundException, InterruptedException, ParseException {
        orderService.Save();
    }


    //@RequestMapping(method = RequestMethod.GET, value = "/jpatest")
    @Transactional(transactionManager = "sjHubTransactionManager")
    public void Save() {
        testttRepository.save(new TestTt("2", "3334"));
        testttRepository.save(new TestTt("444444444", "4444444"));
        testttRepository.save(new TestTt("3", "3"));
//        try {
//            testRepository.save(new JdbcTest("1", "1"));
//            testRepository.save(new JdbcTest("2", "2"));
//            testRepository.save(new JdbcTest("33333333", "3333333333333333333"));
//            testRepository.save(new JdbcTest("4", "4"));
//        }catch (DataAccessException ex){
//            throw new RuntimeException(ex);
//        }

    }


    @RequestMapping(method= RequestMethod.GET,value="/localdatetimetest")
    public void localDateTimeTest(){
        LocalDateTime localDateTime=LocalDateTime.now();
        log.info(localDateTime.toString());
        log.info(localDateTime.toLocalDate().atStartOfDay().toString());
    }



}
