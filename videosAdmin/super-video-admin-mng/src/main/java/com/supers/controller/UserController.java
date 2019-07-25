package com.supers.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.supers.pojo.Users;
import com.supers.service.UsersService;
import com.supers.utils.MD5Utils;
import com.supers.utils.SuperJSONResult;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UsersService usersService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@PostMapping("/login")
	@ResponseBody
	public SuperJSONResult userLogin(String username, String password, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//1判断传入到后台的用户名和密码是否正确
		//2利用用户名和密码后台查询是否正确
		//3将用户信息放到session中或者放到redis中
		if(StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
			return SuperJSONResult.errorMsg("用户密码未填");
		}
		String passwordMd5 = MD5Utils.getMD5Str(password).trim();
		 
		Users user = usersService.getUser(username, passwordMd5);
		String passwordDB = user.getPassword().trim();
		if (!passwordDB.equals(passwordMd5)) {
			return SuperJSONResult.errorMsg("用户密码错误");
		}
		request.getSession().setAttribute("sessionUser", user);
		return SuperJSONResult.ok();
	}
}
