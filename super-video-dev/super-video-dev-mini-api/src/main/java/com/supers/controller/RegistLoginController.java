package com.supers.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.supers.pojo.Users;
import com.supers.pojo.vo.UsersVo;
import com.supers.service.UserService;
import com.supers.utils.MD5Utils;
import com.supers.utils.SuperJSONResult;

@RestController
@Api(value="用户登录注册接口", tags={"注册登录的controller"})
public class RegistLoginController extends ControllerRedis{

	@Autowired
	private UserService userService;
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value="用户注册", notes ="用户注册的接口")
	@PostMapping("/regist")
	public SuperJSONResult regist(@RequestBody Users user) throws Exception {
		//1判读用户密码不能为空
		if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
			return SuperJSONResult.errorMsg("用户名密码不能为空");
		}
		//2判断用户名是否存在
		boolean queryUsernameIsExists = userService.queryUsernameIsExists(user.getUsername());
		
		//3保存用户注册信息
		if (!queryUsernameIsExists) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setFollowCounts(0);
			user.setReceiveLikeCounts(0);
			userService.saveUser(user);
		} else {
			return SuperJSONResult.errorMsg("用户已存在,换一个试试");
		}
		UsersVo usersVo = getUserTokenTOSession(user);
		return SuperJSONResult.ok(usersVo);
	}
	
	public UsersVo getUserTokenTOSession(Users users) {
		String userToken = UUID.randomUUID().toString();
		redisOperator.set(ControllerRedis.USER_REDIS_SESSION+":"+users.getId(), userToken, 1000 * 60 * 10);
		UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(users, usersVo);
		usersVo.setUserToken(userToken);
		return usersVo;
	}
	
	/**
	 * 用户注册
	 * @throws Exception 
	 */
	@ApiOperation(value="用户登录", notes= "登录接口")
	@PostMapping("/login")
	public SuperJSONResult login(@RequestBody Users user) throws Exception {
		//判断用户名密码是否为空
		if (StringUtils.isEmpty(user.getUsername())|| StringUtils.isEmpty(user.getPassword())) {
			return SuperJSONResult.errorMsg("用户名密码不能为空");
		}  
		//2判断用户名是否存在
		/*boolean queryUsernameIsExists = userService.queryUsernameIsExists(user.getUsername());
		if (!queryUsernameIsExists) {
			return SuperJSONResult.errorMsg("用户不存在，请先注册");
		}*/
		//查询密码
		Users users = userService.queryPassword(user.getUsername());
		if (StringUtils.isEmpty(users.getUsername())) {
			return SuperJSONResult.errorMsg("用户不存在，请先注册");
		}
		if (!MD5Utils.getMD5Str(user.getPassword()).equals(users.getPassword())) {
			return SuperJSONResult.errorMsg("密码不正确请重新输入");
		}
		UsersVo usersVo = getUserTokenTOSession(users);

		return SuperJSONResult.ok(usersVo);
	}
	
	/**
	 * 用户注销
	 */
	@ApiOperation(value="用户注销", notes= "注销接口")
	@PostMapping("/logout")
	@ApiImplicitParam(name="userId", value="用户Id", required = true, 
		dataType="String", paramType="query")
	public SuperJSONResult logout(String userId) {
		redisOperator.del(ControllerRedis.USER_REDIS_SESSION+":"+userId);
		return SuperJSONResult.ok("注销成功");
	}
}
