package com.supers.service;

import java.util.List;

import com.supers.pojo.Videos;
import com.supers.utils.PagedResult;

public interface VideoService {

	/**
	 * 将视频信息插入表中
	 */
	public String insert(Videos videos);

	/**
	 * 修改信息不为空的进行修改
	 * @param videoId
	 * @param uploadPathDB
	 */
	public void updateById(Videos video);
	
	/**
	 * 分页查询Videos信息
	 * @param pageSize2 
	 * @param video 
	 */
	public PagedResult queryAllVideo(Videos video, String userId, Integer page, Integer pageSize, Integer pageSize2);

	/**
	 * 热搜词查询
	 * @return
	 */
	public List<String> getHotContent();
	
	/**
	 * 视频喜欢点赞增加
	 */
	public void addVideoLikeCounts(String userId, String videoId, String videoCreateId);
	
	/**
	 * 视频喜欢取消点赞减少
	 */
	public void reduceVideoLikeCounts(String userId, String videoId, String videoCreateId);

	/**
	 * 查询收藏的视频
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult collectionAllVideo(String userId, Integer page,
			Integer pageSize);

	/**
	 * 关注人的视频查询
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult followAllVideo(String userId, Integer page,
			Integer pageSize);
}
