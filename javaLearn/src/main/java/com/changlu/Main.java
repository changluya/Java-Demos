/**
 * @description TODO
 * @author changlu
 * @date 2024/06/14 16:56
 * @version 1.0
 */
package com.changlu;

import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: changlu
 * @Date: 16:56
 */
public class Main {
    private static String EMAIL_PATTERN = "^([a-z0-9A-Z_]+[-|\\.]?)+[a-z0-9A-Z_]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)*\\.)+[a-zA-Z]{2,}$";
    private final static Pattern EMAIL_PATTERN1 = Pattern.compile(EMAIL_PATTERN);

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
