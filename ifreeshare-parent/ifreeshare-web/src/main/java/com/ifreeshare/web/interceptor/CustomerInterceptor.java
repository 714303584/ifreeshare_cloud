package com.ifreeshare.web.interceptor;

import com.alibaba.fastjson2.JSON;
import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.tools.finals.TokenPayloadKeys;
import com.ifreeshare.web.ResponseCode;
import com.ifreeshare.web.HeaderBean;
import com.ifreeshare.web.ResultData;
import com.ifreeshare.web.ThreadLocalHeaderBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class CustomerInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(CustomerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("CustomerInterceptor start");
        //获取token -- 当前版本仅仅获取token
        String token = request.getHeader("token");
        //token 不为空
        if(!StringUtils.isEmpty(token)){
          Map<String,Object> tokenData = JwtUtils.getTokenData(token);
          String userId = tokenData.get(TokenPayloadKeys.USER_ID).toString();
            HeaderBean headerBean = new HeaderBean();
            headerBean.setUserId(Long.parseLong(userId));
            ThreadLocalHeaderBean.set(headerBean);
        }else{
          ResultData<String> faildResultData =  new ResultData<String>(
                    ResponseCode.NO_LOGIN_ERROR.getCode().toString(), ResponseCode.NO_LOGIN_ERROR.getMsg(), null);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response
                  .getWriter()
                  .print(JSON.toJSONString(faildResultData));
              return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("afterCompletion: clean threadLocalHeaderBean");
        ThreadLocalHeaderBean.clean();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
