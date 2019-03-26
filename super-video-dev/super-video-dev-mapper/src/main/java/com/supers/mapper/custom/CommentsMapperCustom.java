package com.supers.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.supers.pojo.vo.CommentsVo;
import com.supers.utils.MyMapper;

public interface CommentsMapperCustom extends MyMapper<CommentsVo> {

	/**
	 * 查询评论
	 * @param videoId
	 * @return
	 */
	List<CommentsVo> getQueryAllComment(@Param("videoId") String videoId);
}