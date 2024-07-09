package com.changlu;

import com.alibaba.fastjson.JSON;
import com.dtstack.lang.base.Strings;
import com.dtstack.lang.exception.BizException;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author 猫爸@dtstack.Inc
 * @mission ==> 让数据产生价值
 * @department 产品事业部/技术研发中心
 * @date 2020-02-28 14:10
 */
public class PoolHttpClient {

    /**
     *  10秒
     */
    private static int SocketTimeout = 10000;


    /**
     *  5秒
     */
    private static int ConnectTimeout = 5000;

    /**
     * 将最大连接数增加到100
     */
    private static int maxTotal = 100;

    /**
     * 将每个路由基础的连接增加到20
     */
    private static int maxPerRoute = 20;

    private static Boolean SetTimeOut = true;

    private static CloseableHttpClient httpClient = null;

    private static String code = "UTF-8";

    static {
        try {
            httpClient = getHttpClient();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private static CloseableHttpClient getHttpClient() throws Exception {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", new SSLConnectionSocketFactory(VerifySSLContext.createIgnoreVerifySSL())).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        return HttpClients.custom()
                .setConnectionManager(cm).setRetryHandler(new RdosHttpRequestRetryHandler()).build();
    }

    public static String post(String url, String bodyData, Map<String, Object> cookies) {
        return post(url, bodyData, cookies, null);
    }

    public static String postWithTimeout(String url, String bodyData, Map<String, Object> cookies, int socketTimeout, int connectTimeout) {
        return postWithTimeout(url, bodyData, cookies, null, socketTimeout, connectTimeout);
    }

    public static String post(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers) {
        HttpPost httpPost = new HttpPost(url);
        setPostMethod(httpPost,url, bodyData, cookies, headers);
        setConfig(httpPost);
        return execute(httpPost);
    }

    public static String postWithTimeout(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers, int socketTimeout, int connectTimeout) {
        HttpPost httpPost = new HttpPost(url);
        setPostMethod(httpPost,url, bodyData, cookies, headers);
        socketTimeout = socketTimeout <= 0 ? 300000 : socketTimeout;
        connectTimeout = connectTimeout <= 0 ? 2000 : connectTimeout;
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                // 设置请求和传输超时时间
                .setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);
        return execute(httpPost);
    }

    private static void setPostMethod(HttpPost httpPost,String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers) {
        if (cookies != null && cookies.size() > 0) {
            httpPost.addHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        if (Strings.isNotBlank(bodyData)) {
            StringEntity stringEntity = new StringEntity(bodyData, code);
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        }
    }

    private static String execute(HttpPost httpPost) {
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = null;
            // 请求数据
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            // FIXME 暂时不从header读取
            String result = EntityUtils.toString(entity, code);
            if (status == HttpStatus.SC_OK) {
                responseBody = result;
            } else if (status == HttpStatus.SC_UNAUTHORIZED) {
                throw new BizException("登陆状态失效");
            } else {
                logger.error("request url:{} fail:{}", httpPost.getURI().toString(), result);
                return null;
            }
            return responseBody;
        } catch (Exception e) {
            logger.error("url:" + httpPost.getURI().toString() + "--->http request error:", e);
        }
        return null;
    }

    public static String post(String url, Map<String, Object> paramMap, Map<String, Object> cookies) throws IOException {
        String bodyData = "";
        if (paramMap != null && paramMap.size() > 0) {
            bodyData = JSON.toJSONString(paramMap);
        }
        return post(url, bodyData, cookies);
    }

    public static String postWithParamter(String url, ArrayList<BasicNameValuePair> data) throws UnsupportedEncodingException {
        return postWithParamter(url,data);
    }

    public static String postWithParamter(String url, ArrayList<BasicNameValuePair> data,String contentEncoding) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        //首先将参数设置为utf-8的形式
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, code);
        if (StringUtils.isNotBlank(contentEncoding) && !"no".equals(contentEncoding)) {
            entity.setContentEncoding(contentEncoding);
        }
        entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        httpPost.setEntity(entity);
        setConfig(httpPost);
        return execute(httpPost);
    }

    private static void setConfig(HttpRequestBase httpRequest) {
        if (SetTimeOut) {
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SocketTimeout)
                    .setConnectTimeout(ConnectTimeout).build();
            httpRequest.setConfig(requestConfig);
        }
    }

    public static String get(String url, Map<String, Object> cookies) {
        HttpGet httpGet = new HttpGet(url);
        setConfig(httpGet);
        if (cookies != null && cookies.size() > 0) {
            httpGet.setHeader("Cookie", getCookieFormate(cookies));
        }
        try (CloseableHttpResponse response = httpClient.execute(httpGet);) {
            String respBody = null;
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, Charsets.UTF_8);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                respBody = result;
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new BizException("登陆状态失效");
            } else {
                logger.error("request url:{} fail:{}", url, result);
            }
            return respBody;
        } catch (Exception e) {
            logger.error("url:{}--->http request error:{}", url, e);
        }
        return null;
    }

    public static String getCookieFormate(Map<String, Object> cookies) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Object>> sets = cookies.entrySet();
        for (Map.Entry<String, Object> s : sets) {
            sb.append(s.getKey() + "=" + s.getValue().toString() + ";");
        }
        return sb.toString();
    }

    public static String formPost(String url, Map<String, Object> params, Map<String, Object> cookies,Map<String, Object> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (cookies != null && cookies.size() > 0) {
            httpPost.addHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            nvps.add(new BasicNameValuePair(key, (String)params.get(key)));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        setConfig(httpPost);
        return execute(httpPost);
    }

    public static String post(String url, String bodyData, Map<String, Object> cookies, Map<String, Object> headers, Boolean isForm, Map<String, Object> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        if (cookies != null && cookies.size() > 0) {
            httpPost.addHeader("Cookie", getCookieFormate(cookies));
        }
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        if (Strings.isNotBlank(bodyData)) {
            if (isForm) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();

                Set<String> keySet = params.keySet();
                for(String key : keySet) {
                    nvps.add(new BasicNameValuePair(key, (String)params.get(key)));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } else {
                StringEntity stringEntity = new StringEntity(bodyData, code);
                stringEntity.setContentEncoding("UTF-8");
                stringEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(stringEntity);
            }

        }
        setConfig(httpPost);
        return execute(httpPost);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        ArrayList<BasicNameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("client_id", "clientId"));
        data.add(new BasicNameValuePair("client_secret", "clientSecret"));
        data.add(new BasicNameValuePair("grant_type", "authorization_code"));
        data.add(new BasicNameValuePair("code", "xxx"));
        String s = PoolHttpClient.postWithParamter("https://www.fjsjkj.com:8081/sso/oauth/2/token", data, "UTF-8");
        System.out.println(s);
    }

}
