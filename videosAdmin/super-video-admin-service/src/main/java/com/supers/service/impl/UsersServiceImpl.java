package com.supers.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supers.mapper.UsersMapper;
import com.supers.pojo.Users;
import com.supers.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersMapper usersMapper;

	@Override
	public Users getUser(String username, String password) {
		Users user = usersMapper.getUsers(username, password);
		return user;
	}

	
}
