package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by geely
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    public static void writeLoginToken(HttpServletResponse response,String token){

        Cookie ck = new Cookie(COOKIE_NAME,token);

        //设置可访问该Cookie的域名如user.happymmall.com。。。
        ck.setDomain(COOKIE_DOMAIN);
        ck.setHttpOnly(true);
        //单位是秒，如果是-1的话代表永久有效，
        // 如果MaxAge不设置的话代表写入内存中，不会写入磁盘，当前页面有效
        ck.setMaxAge(60*60*24*365);

        //设置在根目录
        ck.setPath("/");

        log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);

    }

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();

        if(cookies != null){
            for(Cookie cks : cookies){
                log.info("read cookieName:{},cookieValue:{}",cks.getName(),cks.getValue());
                if(StringUtils.equals(cks.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{},cookieValue:{}",cks.getName(),cks.getValue());
                    return cks.getValue();
                }
            }
        }
        return null;
    }


    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){

        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setPath("/");
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setMaxAge(0);//设置为0就代表删除cookie
                    log.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }

    }


}
