package com.supers.service;


import org.springframework.stereotype.Service;

import com.supers.pojo.Users;

@Service
public interface UsersService {

	/**
	 * 登录验证用户信息
	 */
	public Users getUser(String username, String password);
}
