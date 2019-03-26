package com.supers.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supers.service.BgmService;
import com.supers.utils.SuperJSONResult;

@RestController
@Api(value="背景音乐接口", tags={"背景音乐的controller"})
@RequestMapping("/bgm")
public class BgmController extends ControllerRedis{

	@Autowired
	private BgmService bgmService;
	
	/**
	 * 查询所有背景音乐
	 */
	@ApiOperation(value="用户查询用户信息", notes= "用户信息接口")
	@PostMapping("/query")
	public SuperJSONResult queryUser() {
	 
		return SuperJSONResult.ok(bgmService.queryBgmList());
	}
}
