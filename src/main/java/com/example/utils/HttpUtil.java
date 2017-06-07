package com.example.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by gaoqichao on 15-11-4.
 */
public class HttpUtil {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    private static final String DEFAUTL_CHARSET = "utf-8";

    private HttpUtil() {

    }

    /**
     * post方法请求数据
     *
     * @param requestUrl 请求url
     * @return http相应结果
     */
    public static String post(String requestUrl) {
        return post(requestUrl, StringUtils.EMPTY);
    }

    /**
     * post方法请求数据
     *
     * @param requestUrl 请求url
     * @param data       请求数据
     * @return http相应结果
     */
    public static String post(String requestUrl, String data) {
        LOG.info("=======================请求url:" + requestUrl);
        if (StringUtils.isNotEmpty(data)) {
            LOG.info("=======================请求参数:" + data);
        }

        HttpPost post = new HttpPost(requestUrl);
        if (StringUtils.isNotEmpty(data)) {
            StringEntity entity = new StringEntity(data, DEFAUTL_CHARSET);
            entity.setContentType("application/json");
            post.setEntity(entity);
        }

        return getResult(post, requestUrl);
    }


    /**
     * post方法请求数据
     *
     * @param requestUrl 请求url
     * @param params     请求数据
     * @return http相应结果
     */
    public static String post(String requestUrl, List<NameValuePair> params) throws UnsupportedEncodingException {
        LOG.info("=======================请求url:" + requestUrl);

        HttpPost post = new HttpPost(requestUrl);
        if (CollectionUtils.isNotEmpty(params)) {
            post.setEntity(new UrlEncodedFormEntity(params, DEFAUTL_CHARSET));
        }

        return getResult(post, requestUrl);
    }

    /**
     * 取得http请求结果
     *
     * @param post       post
     * @param requestUrl url
     * @return 响应结果
     */
    private static String getResult(HttpPost post, String requestUrl) {
        CloseableHttpClient client = HttpClients.createDefault();
        String result = null;
        try {

            CloseableHttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            if (null != httpEntity) {
                result = EntityUtils.toString(httpEntity, DEFAUTL_CHARSET);
            }
        } catch (IOException e) {
            LOG.error("请求发生错误: " + requestUrl, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                LOG.error("关闭http连接发生错误", e);
            }
        }
        return result;
    }
}
