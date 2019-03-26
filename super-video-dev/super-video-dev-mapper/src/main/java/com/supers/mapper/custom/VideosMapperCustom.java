package com.supers.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.supers.pojo.Videos;
import com.supers.pojo.vo.VideoVo;
import com.supers.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {
	
	public List<VideoVo> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);
	
	/**
	 * 用户点赞video like count加1
	 * @param videoId
	 */
	public void addVideoLikeCounts(@Param("videoId") String videoId);
	
	/**
	 * 用户取消点赞video like count加1
	 * @param videoId
	 */
	public void reduceVideoLikeCounts(@Param("videoId") String videoId);

	/**
	 * 获取关注的视频信息
	 * @param userId
	 * @return
	 */
	public List<VideoVo> collectionAllVideos(@Param("userId") String userId);

	/**
	 * 获取关注的视频信息
	 * @param userId
	 * @return
	 */
	public List<VideoVo> followAllVideos(@Param("userId") String userId);
}