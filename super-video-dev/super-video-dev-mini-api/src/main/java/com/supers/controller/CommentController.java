package com.supers.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supers.pojo.Comments;
import com.supers.service.CommentService;
import com.supers.utils.PagedResult;
import com.supers.utils.SuperJSONResult;

@RestController
@Api(value="评论", tags="评论的controller")
@RequestMapping("/comment")
public class CommentController {

	@Autowired
	private CommentService commentService;
	/**
	 * 用户举报视频
	 */
	/*@ApiOperation(value="用户举报视频", notes="视频举报接口")
	@ApiImplicitParam(name="userId", value = "用户id", required = true,
	dataType="String", paramType="query")*/
	@PostMapping("/userComment")
	public SuperJSONResult userComment(@RequestBody Comments comments) {
		//举报用户的userId，  被举报的用户userId， 视频id，举报类型，举报详情，举报时间
		if (StringUtils.isBlank(comments.getComment()) || 
				StringUtils.isBlank(comments.getVideoId()) ||
				StringUtils.isBlank(comments.getFromUserId()) ) {
			return SuperJSONResult.errorMsg("信息不完善,或者用户没有登录");
		} 
		commentService.insertComments(comments);
		return SuperJSONResult.ok("评论成功");
	}
	
	/**
	 * 分页查询评论
	 */
	@PostMapping("/queryAllComment")
	@ApiOperation(value="分页查询评论", notes="分页查询评论接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="videoId", value="视频Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="page", value="第几页", required = true, 
				dataType="Integer", paramType="form"),
		@ApiImplicitParam(name="pageSize", value="每页显示条数", required = true, 
				dataType="Integer", paramType="form")
				 
	})
	public SuperJSONResult getQueryAllComment(String videoId, Integer page, Integer pageSize) {
		//利用 videoId查询 评论
		if (StringUtils.isBlank(page.toString())) {
			page = 1;
		}
		if (StringUtils.isBlank(pageSize.toString())) {
			pageSize = 5;
		}
		PagedResult allComment = commentService.getQueryComment(videoId, page, pageSize);
		return SuperJSONResult.ok(allComment);
	}
}
