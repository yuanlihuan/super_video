package com.supers.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.supers.conf.ResourceConfig;
import com.supers.pojo.Users;
import com.supers.pojo.vo.UsersAndVideoVo;
import com.supers.pojo.vo.UsersVo;
import com.supers.service.UserService;
import com.supers.utils.ConstantConfig;
import com.supers.utils.ImageUtils;
import com.supers.utils.SuperJSONResult;

@RestController
@Api(value="用户业务接口", tags={"用户业务的controller"})
@RequestMapping("/user")
public class UserController extends ControllerRedis{

	@Autowired
	private UserService userService;
	
	@Autowired
	private ResourceConfig resourceConfig;
	/**
	 * 用户上传头像
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("null")
	@ApiOperation(value="用户上传头像", notes= "上传头像接口")
	@PostMapping("/uploadFace")
	@ApiImplicitParam(name="userId", value="用户Id", required = true, 
		dataType="String", paramType="query")
	public SuperJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws FileNotFoundException {
		
		if(StringUtils.isEmpty(userId)) {
			return SuperJSONResult.errorMsg("上传用户未登录");
		}
		String fileSpace = resourceConfig.getFileFace();
		String uploadPath = "/" + userId + "/face/";
		String uploadPathDb = null;
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (files != null || files.length > 0) {
				//获取文件名
				String filename = files[0].getOriginalFilename();
		        String fileNamejpg = filename.substring(filename.lastIndexOf(".") + 1);
		        String uuid = UUID.randomUUID().toString();
		        //更改文件名
		        String fileNameNew = uuid + "." +fileNamejpg;
				if (!StringUtils.isEmpty(filename)) {
					//文件上传的最终路径
					String finalFacePath = fileSpace + uploadPath + fileNameNew;
					//设置数据库保存的路径
					uploadPathDb = uploadPath + fileNameNew;;
					File file = new File(finalFacePath);
					if (file.getParentFile() != null && !file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
					//将图片进行缩略文件名
					filename = uuid+ "." +fileNamejpg;
					String finalFacePathMin = fileSpace+ uploadPath + filename;
					ImageUtils.scale2(finalFacePath, finalFacePathMin, 90, 90, true);
				}
			} else {
				return SuperJSONResult.errorMsg("上传失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return SuperJSONResult.errorMsg("上传失败");
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//将路径保存再数据库
		Users users = new Users();
		users.setId(userId);
		users.setFaceImage(uploadPathDb);
		SuperJSONResult result = userService.updateUser(users);
		if (result.getStatus() == 200) {
			return SuperJSONResult.ok(uploadPathDb);
		} else {
			return SuperJSONResult.errorMsg("上传失败重新上传");
		}
	}
	
	/**
	 * 用户信息
	 */
	@ApiOperation(value="用户查询用户信息", notes= "用户信息接口")
	@PostMapping("/query")
	@ApiImplicitParam(name="userId", value="用户Id", required = true, 
		dataType="String", paramType="query")
	public SuperJSONResult queryUser(String userId, String fanId) {
		if(StringUtils.isEmpty(userId)) {
			return SuperJSONResult.errorMsg("上传用户未登录");
		}
		Users user = userService.queryUser(userId);
		UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(user, usersVo);
		
		boolean isNotFollow = userService.getUserFans(userId, fanId);
		usersVo.setIsNotFollow(isNotFollow);
		return SuperJSONResult.ok(usersVo);
	}
	
	/**
	 * 查询用户信息和视频是否已经点赞
	 */
	@ApiOperation(value="获取用户和视频点赞信息", notes="获取用户和视频点赞信息接口")
	@PostMapping("/videoAndUserDetail")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value = "用户Id", required=true,
				dataType="String", paramType="query"),
		@ApiImplicitParam(name="videoId", value = "视频Id", required=true,
				dataType="String", paramType="query"),
		@ApiImplicitParam(name="publishUserId", value = "发布视频用户Id", required=true,
		dataType="String", paramType="query")
	})
	public SuperJSONResult videoAndUserDetail(String userId, String videoId, String  publishUserId) {
		if(StringUtils.isEmpty(publishUserId)) {
			return SuperJSONResult.errorMsg("无用户信息");
		}
		Users user = userService.queryUser(publishUserId);
		UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(user, usersVo);
		
		Boolean bol = userService.getVideoAndUserDetail(userId, videoId);
		UsersAndVideoVo usersAndVideoVo = new UsersAndVideoVo();
		usersAndVideoVo.setBol(bol);
		usersAndVideoVo.setUsersVo(usersVo);
		return SuperJSONResult.ok(usersAndVideoVo);
	}
	
	/**
	 * 关注/和取消关注
	 */
	@ApiOperation(value="获取用户和视频点赞信息", notes="获取用户和视频点赞信息接口")
	@PostMapping("/fanFollow")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value = "用户Id", required=true,
				dataType="String", paramType="query"),
		@ApiImplicitParam(name="fanId", value = "粉丝Id", required=true,
				dataType="String", paramType="query"),
		@ApiImplicitParam(name="isNotFollow", value = "是否关注", required=true,
				dataType="Integer", paramType="query"),
		 
	})
	public SuperJSONResult fanFollow(String userId, String fanId, Integer isNotFollow) {
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)) {
			return SuperJSONResult.errorMsg("无用户信息");
		}
		if (userId.equals(fanId)) {
			return SuperJSONResult.errorMsg("不能关注自己");
		}
		if(isNotFollow == 1) {			
			userService.insertFanFollow(userId, fanId);
			return SuperJSONResult.ok();
		} else if(isNotFollow == 0) {			
			userService.delFanFollow(userId, fanId);
			return SuperJSONResult.ok();
		}
		return null;
	}
	
	/**
	 * 查询关注信息
	 */
	 
}
