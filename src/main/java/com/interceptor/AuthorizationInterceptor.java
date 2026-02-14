package com.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.HttpStatus;

import com.annotation.IgnoreAuth;
import com.entity.EIException;
import com.entity.TokenEntity;
import com.service.TokenService;
import com.utils.R;

/**
 * 权限(Token)验证
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    public static final String LOGIN_TOKEN_KEY = "Token";

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //支持跨域请求
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,request-source,Token, Origin,imgType, Content-Type, cache-control,postman-token,Cookie, Accept,authorization");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return false;
        }

        IgnoreAuth annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreAuth.class);
        } else {
            return true;
        }

        /**
         * 不需要验证权限的方法直接放过
         */
        if(annotation!=null) {
            return true;
        }

        //从header中获取token
        String token = request.getHeader(LOGIN_TOKEN_KEY);

        // 添加调试日志
        System.out.println("=== Token验证调试信息 ===");
        System.out.println("请求URL: " + request.getRequestURL());
        System.out.println("请求方法: " + request.getMethod());
        System.out.println("Token: " + token);

        TokenEntity tokenEntity = null;
        if(StringUtils.isNotBlank(token)) {
            try {
                tokenEntity = tokenService.getTokenEntity(token);
                System.out.println("Token实体: " + (tokenEntity != null ? "找到" : "未找到"));
                if (tokenEntity != null) {
                    System.out.println("Token用户: " + tokenEntity.getUsername());
                    System.out.println("Token过期时间: " + tokenEntity.getExpiratedtime());
                }
            } catch (Exception e) {
                System.out.println("Token查询异常: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if(tokenEntity != null) {
            // 检查token是否过期
            if (tokenEntity.getExpiratedtime() != null &&
                    tokenEntity.getExpiratedtime().getTime() < System.currentTimeMillis()) {
                System.out.println("Token已过期");
                return unauthorizedResponse(response, "登录已过期，请重新登录");
            }

            request.getSession().setAttribute("userId", tokenEntity.getUserid());
            request.getSession().setAttribute("role", tokenEntity.getRole());
            request.getSession().setAttribute("tableName", tokenEntity.getTablename());
            request.getSession().setAttribute("username", tokenEntity.getUsername());

            System.out.println("Token验证成功，用户: " + tokenEntity.getUsername());
            return true;
        }

        System.out.println("Token验证失败");
        return unauthorizedResponse(response, "请先登录");
    }

    private boolean unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSONObject.toJSONString(R.error(401, message)));
        } finally {
            if(writer != null){
                writer.close();
            }
        }
        return false;
    }
}