package com.supers.service.impl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.supers.mapper.UsersFansMapper;
import com.supers.mapper.UsersLikeVideosMapper;
import com.supers.mapper.UsersMapper;
import com.supers.pojo.Users;
import com.supers.pojo.UsersFans;
import com.supers.pojo.UsersLikeVideos;
import com.supers.service.UserService;
import com.supers.utils.SuperJSONResult;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private UsersFansMapper usersFansMapper;
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExists(String username) {
		Users users = new Users();
		users.setUsername(username);
		Users result = usersMapper.selectOne(users);
		return result == null? false:true;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void saveUser(Users user) {
		user.setId(sid.nextShort());
		usersMapper.insert(user);
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Users queryPassword(String username) {
		Users users = new Users();
		users.setUsername(username);
		Users result = usersMapper.selectOne(users);
		return result;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public SuperJSONResult updateUser(Users users) {
		 Example userExample = new Example(Users.class);
		 Criteria criteria = userExample.createCriteria();
		 criteria.andEqualTo("id", users.getId());
		 //criteria.andEqualTo("faceImage", users.getFaceImage());
		 int  updateUser = usersMapper.updateByExampleSelective(users, userExample);
		 //TODO 没有异常处理
		 if (updateUser != 1) {
			 SuperJSONResult.errorMsg("更改头像图片失败");
		 }
		 return SuperJSONResult.ok();
	}

	@Override
	@Transactional(propagation=Propagation.SUPPORTS)
	public Users queryUser(String userId) {
		Example example = new Example(Users.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("id", userId);
		Users users = usersMapper.selectOneByExample(example);
		return users;
	}

	@Override
	@Transactional(propagation=Propagation.SUPPORTS)
	public Boolean getVideoAndUserDetail(String userId, String videoId) {
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void insertFanFollow(String userId, String fanId) {
		//1插入数据，插入成功就汇修改关注数和被关注数
		UsersFans usersFans = new UsersFans();
		usersFans.setId(sid.nextShort());
		usersFans.setUserId(userId);
		usersFans.setFanId(fanId);
		int insert = usersFansMapper.insert(usersFans);
		if (insert == 1) {
			usersMapper.addFansCounts(userId);
			usersMapper.addFollowCounts(fanId);
			
		}
	}

	@Override
	public void delFanFollow(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		int deleteByExample = usersFansMapper.deleteByExample(example);
		if (deleteByExample == 1) {
			
			usersMapper.reduceFansCounts(userId);
			usersMapper.reduceFollowCounts(fanId);
		}
	}

	@Override
	public boolean getUserFans(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		List<UsersFans> list = usersFansMapper.selectByExample(example);
		if (list != null && list.size() >= 1) {
			return true;
		}
		return false;
	}

}
