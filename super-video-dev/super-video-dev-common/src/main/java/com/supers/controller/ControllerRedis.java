package com.supers.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.supers.utils.RedisOperator;

 
public class ControllerRedis {
	
	@Autowired
	public RedisOperator redisOperator;
	
	public static final String USER_REDIS_SESSION = "user-redis-session";

}
