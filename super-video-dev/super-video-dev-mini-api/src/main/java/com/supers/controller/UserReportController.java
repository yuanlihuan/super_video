package com.supers.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supers.pojo.UsersReport;
import com.supers.service.UserReportService;
import com.supers.utils.SuperJSONResult;

@RestController
@Api(value="用户举报", tags="用户举报的controller")
@RequestMapping("/userReport")
public class UserReportController {

	@Autowired
	private UserReportService userReportService;
	/**
	 * 用户举报视频
	 */
	@ApiOperation(value="用户举报视频", notes="视频举报接口")
	@ApiImplicitParam(name="userId", value = "用户id", required = true,
	dataType="String", paramType="query")
	@PostMapping("/reportVideo")
	public SuperJSONResult userReportVideo(@RequestBody UsersReport usersReport) {
		//举报用户的userId，  被举报的用户userId， 视频id，举报类型，举报详情，举报时间
		if (StringUtils.isBlank(usersReport.getDealUserId()) || 
				StringUtils.isBlank(usersReport.getDealVideoId()) ||
				StringUtils.isBlank(usersReport.getUserid()) ) {
			return SuperJSONResult.errorMsg("举报信息不完善");
		} 
		userReportService.insertUserReportVideo(usersReport);
		return SuperJSONResult.ok("举报成功");
	}
}
