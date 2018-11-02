package com.mmall.controller.common.Interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");

        //请求中Controller的方法名
        HandlerMethod handlerMethod = (HandlerMethod)o;

        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap  = httpServletRequest.getParameterMap();
        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()){
           Map.Entry entry = (Map.Entry) iterator.next();
           String mapKey = (String) entry.getKey();

           String mapValue = StringUtils.EMPTY;

           //value返回的是一个String[]
            Object obj = entry.getValue();
            if(obj instanceof String[]){
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }

            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
            log.info("拦截器拦截到请求，className:{},methodName:{}",className,methodName);
            return true;
        }
        log.info("拦截器拦截到请求，className:{},methodName:{},param:{}",className,methodName,requestParamBuffer.toString());

        User user = null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr,User.class);
        }

        if(user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
            //返回false，即不会调用controller里面的方法
            httpServletResponse.reset(); //不添加会报异常，getWriter（）
            httpServletResponse.setCharacterEncoding("UTF-8"); //会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");

            PrintWriter out = httpServletResponse.getWriter();

            if(user == null){
                //处理富文本上传文件的特殊情况
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                }else{
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }
            }else{
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户无权限操作")));
                }
            }
            out.flush();
            out.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
