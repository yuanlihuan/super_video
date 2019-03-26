package com.supers.mapper;

import org.apache.ibatis.annotations.Param;

import com.supers.pojo.Users;
import com.supers.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
	/**
	 * 点赞的话发布视频用户like总数加
	 */
	public void addUserLikeCounts(@Param("userId") String userId);
	
	/**
	 * 取消点赞的话发布视频用户like总数加
	 */
	public void reduceUserLikeCounts(@Param("userId") String userId);

	/**
	 * 增加粉丝数
	 * @param userId
	 */
	public void addFansCounts(@Param("userId")String userId);

	/**
	 * 减少粉丝数
	 * @param userId
	 */
	public void reduceFansCounts(@Param("userId")String userId);

	/**
	 * 增加关注数
	 * @param userId
	 */
	public void addFollowCounts(@Param("fanId")String fanId);
	
	/**
	 * 减少关注
	 * @param userId
	 */
	public void reduceFollowCounts(@Param("fanId")String fanId);
}