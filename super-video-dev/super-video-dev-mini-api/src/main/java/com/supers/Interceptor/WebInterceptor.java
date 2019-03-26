package com.supers.Interceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.supers.controller.ControllerRedis;
import com.supers.utils.JsonUtils;
import com.supers.utils.RedisOperator;
import com.supers.utils.SuperJSONResult;

public class WebInterceptor implements HandlerInterceptor {

	@Autowired
	private RedisOperator redisOperator;
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String userId = request.getHeader("userId");
		String userToken = request.getHeader("userToken");
 
		if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
			String redisToken = redisOperator.get(ControllerRedis.USER_REDIS_SESSION+":"+userId);
			if (StringUtils.isBlank(redisToken)) {
				returnErrorResponse(response,  new SuperJSONResult().errorTokenMsg("登录过期..."));
				return false;
			} else if (!redisToken.equals(userToken)) {
				returnErrorResponse(response,  new SuperJSONResult().errorTokenMsg("被挤出..."));
				return false;
			} else {
				return true;
			}
		} else {
			returnErrorResponse(response, new SuperJSONResult().errorTokenMsg("请登录.."));
			return false;
		}
		//return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	//请求结束后 进行调用  但是在视图被渲染前
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		//HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	//在整个请求结束之后被调用，也就是dispatcherServlet渲染了对应的视图之后
	//主要是用于资源的清理
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		//HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
	public void returnErrorResponse(HttpServletResponse response, SuperJSONResult result) 
			throws IOException, UnsupportedEncodingException {
		OutputStream out=null;
		try{
		    response.setCharacterEncoding("utf-8");
		    response.setContentType("text/json");
		    out = response.getOutputStream();
		    out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
		    out.flush();
		} finally{
		    if(out!=null){
		        out.close();
		    }
		}
	}
}
