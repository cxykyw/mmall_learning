package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.Category;
import com.mmall.pojo.TestPojo;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);

        //取消默认转换的timestemp形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //所有的日期格式都统一为以下格式，即yyyy:MM:dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略在json字符串中存在，但是在java对象中不存在对应的属性的问题
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }


    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try{
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        }catch(Exception e){
            log.warn("Parse object to String error",e);
            return null;
        }
    }


    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try{
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }catch(Exception e){
            log.warn("Parse object to String error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }

    }

    public static <T> T string2Obj(String str,TypeReference<T> tTypeReference){

        if(StringUtils.isEmpty(str) || tTypeReference == null){
            return null;
        }

        try {
            return (T)(tTypeReference.getType().equals(String.class) ? str : objectMapper.readValue(str,tTypeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }

    }


    public static <T> T string2Obj(String str,Class<?> collectionClass,Class<?>...elementClasses){

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);


        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }

    }



    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("kyw@happymmall.com");

        String user1JsonPretty = JsonUtil.obj2StringPretty(u1);
        log.info("user1JsonPretty:{}",user1JsonPretty);

        /*User u2 = new User();
        u2.setId(2);
        u2.setEmail("kywu2@happymmall.com");

        String user1Json = JsonUtil.obj2String(u1);

        String user1JsonPretty = JsonUtil.obj2StringPretty(u1);

        log.info("user1Json:{}",user1Json);
        log.info("user1JsonPretty:{}",user1JsonPretty);

        User user = JsonUtil.string2Obj(user1Json, User.class);
        System.out.println(user.toString()+"=============");

        List<User> userList = Lists.newArrayList();
        userList.add(u1);
        userList.add(u2);

        String userListStr = JsonUtil.obj2StringPretty(userList);
        System.out.println("==============");
        log.info(userListStr);


        List<User> userListObj1 = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>(){});

        List<User> userListObj2 = JsonUtil.string2Obj(userListStr, List.class, User.class);*/

        System.out.println("end");
    }

}





