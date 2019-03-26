package com.supers.service;

import com.supers.pojo.Comments;
import com.supers.utils.PagedResult;

public interface CommentService {

	/**
	 *插入评论信息
	 */
	public void insertComments(Comments comments);

	/**
	 * 分页查询评论
	 * @param videoId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getQueryComment(String videoId, Integer page,
			Integer pageSize);
}
