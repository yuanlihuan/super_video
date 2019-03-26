package com.supers.service.impl;

import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supers.mapper.CommentsMapper;
import com.supers.mapper.custom.CommentsMapperCustom;
import com.supers.pojo.Comments;
import com.supers.pojo.vo.CommentsVo;
import com.supers.service.CommentService;
import com.supers.utils.PagedResult;
import com.supers.utils.TimeAgoUtils;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentsMapper commentsMapper;
	
	@Autowired
	private CommentsMapperCustom commentsMapperCustom;
	
	@Autowired
	private Sid sid;
	
	@Override
	public void insertComments(Comments comments) {
		comments.setId(sid.nextShort());
		//TODO
		comments.setCreateTime(new Date());
		commentsMapper.insert(comments);
	}

	@Override
	public PagedResult getQueryComment(String videoId, Integer page,
			Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		
		List<CommentsVo> queryAll = commentsMapperCustom.getQueryAllComment(videoId);
		
		for (CommentsVo commentsVo : queryAll) {
			String timeAgo = TimeAgoUtils.format(commentsVo.getCreateTime());
			commentsVo.setTimeAgoStr(timeAgo);
		}
		PageInfo<CommentsVo> pageInfo = new PageInfo<CommentsVo>(queryAll);
		PagedResult result = new PagedResult();
		result.setPage(page);
		result.setTotal(pageInfo.getPages());
		result.setRecords(pageInfo.getTotal());
		result.setRows(queryAll);
		return result;
	}

	
	
	
}
