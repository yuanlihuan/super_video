package com.supers.service;

import com.supers.pojo.Users;
import com.supers.utils.SuperJSONResult;

public interface UserService {

	/**
	 * 判断用户名是否存在
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExists(String username);
	
	/**
	 * 保存用户信息
	 * @param user
	 */
	public void saveUser(Users user);

	/**
	 * 查询用户信息
	 * @param username
	 * @return
	 */
	public Users queryPassword(String username);

	/**
	 * 修改用户信息头像
	 * @param users 
	 */
	public SuperJSONResult updateUser(Users users);

	public Users queryUser(String userId);

	/**
	 * 查看用户是否已经点赞该视频
	 * @param videoId
	 * @param likeVideoUserId
	 * @return
	 */
	public Boolean getVideoAndUserDetail(String userId, String videoId);

	/**
	 * 粉丝关注，插入关注数据，修改关注数和被粉丝数
	 * @param userId
	 * @param fanId
	 */
	public void insertFanFollow(String userId, String fanId);

	/**
	 * 粉丝取消关注，删除关注数据，修改关注数和被粉丝数
	 * @param userId
	 * @param fanId
	 */
	public void delFanFollow(String userId, String fanId);

	/**
	 * 动态加载是否意见关注的信息
	 * @param userId
	 * @param fanId
	 * @return
	 */
	public boolean getUserFans(String userId, String fanId);
}
