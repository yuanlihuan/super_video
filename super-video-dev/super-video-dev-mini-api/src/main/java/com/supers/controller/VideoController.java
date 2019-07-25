package com.supers.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.supers.conf.ResourceConfig;
import com.supers.enums.VideoStatusEnums;
import com.supers.pojo.Bgm;
import com.supers.pojo.Videos;
import com.supers.service.BgmService;
import com.supers.service.VideoService;
import com.supers.utils.ConstantConfig;
import com.supers.utils.MergeVideoJpg;
import com.supers.utils.MergeVideoMp3;
import com.supers.utils.PagedResult;
import com.supers.utils.SuperJSONResult;

@RestController
@RequestMapping("/video")
@Api(value="视频相关业务接口", tags={"视频相关业务的controller"})
public class VideoController {

	@Autowired
	private BgmService bgmService;
	
	@Autowired
	private VideoService videoService;
	
	@Autowired
	private ResourceConfig resourceConfig;
	
	/**
	 * 视频上传
	 * @throws IOException 
	 */
	@ApiOperation(value="视频上传", notes= "上传视频接口")
	@PostMapping(value = "/uploadVideo", headers="content-type=multipart/form-data")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
				@ApiImplicitParam(name="bgmId", value="背景音乐Id", required = false, 
				dataType="String", paramType="form"),
				@ApiImplicitParam(name="videoSeconds", value="背景音乐播放长度", required = true, 
				dataType="String", paramType="form"),
				@ApiImplicitParam(name="videoWidth", value="视频宽度", required = true, 
				dataType="String", paramType="form"),
				@ApiImplicitParam(name="videoHeight", value="视频高度", required = true, 
				dataType="String", paramType="form"),
				@ApiImplicitParam(name="desc", value="视频描述", required = false, 
				dataType="String", paramType="form"),
	})
	public SuperJSONResult uploadVideo(String userId, 
			String bgmId, double videoSeconds,
			int videoWidth, int videoHeight,
			String desc,
			@ApiParam(value="短视频", required=true) MultipartFile files) throws IOException {
		
		if(StringUtils.isEmpty(userId)) {
			return SuperJSONResult.errorMsg("上传用户未登录");
		}
		String fileSpace = resourceConfig.getFileVideo();
		String uploadPath = "/" + userId + "/video/";
		//定义截图的路径保存数据库
		String coverPath = "/" + userId + "/video/";
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		//文件上传的最终路径
		String finalVideoPath = null;
		try {
			if (files != null) {
				//获取文件名
				String filename = files.getOriginalFilename();
		        String fileNamejpg = filename.substring(filename.lastIndexOf(".") + 1);
		        String uuid = UUID.randomUUID().toString();
		        //更改文件名
		        String fileNameNew = uuid + "." +fileNamejpg;
				if (!StringUtils.isEmpty(filename)) {
					finalVideoPath = fileSpace + uploadPath + fileNameNew;
					File file = new File(finalVideoPath);
					if (file.getParentFile() != null && !file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					inputStream = files.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return SuperJSONResult.errorMsg("上传视频失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return SuperJSONResult.errorMsg("上传视频失败");
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
		Bgm bgmDetail = null;
		if(!StringUtils.isEmpty(bgmId)) {
			Bgm bgm = new Bgm();
			bgm.setId(bgmId);
			bgmDetail = bgmService.selectByBgmId(bgm);
			if (bgmDetail == null || bgmDetail.getPath() == null) {
				return SuperJSONResult.errorMsg("上传失败，音频文件未查到或者路径为空");
			}
			String ffmpegEXE = ConstantConfig.FFMPEG;
			String mp3Path = resourceConfig.getFileVideo()+bgmDetail.getPath();
			String uuid = UUID.randomUUID().toString();
			uploadPath += uuid + ".mp4";
			//设置数据库保存的路径
			String videoAndMp3Path = resourceConfig.getFileVideo() + uploadPath;
			MergeVideoMp3 tool = new MergeVideoMp3(ffmpegEXE);
			tool.convertor(finalVideoPath, mp3Path, videoSeconds, videoAndMp3Path);
			
			//将视频进行截图
			coverPath += uuid+".jpg";
			MergeVideoJpg videoJpg = new MergeVideoJpg(ffmpegEXE);
			videoJpg.convertor(finalVideoPath, resourceConfig.getFileVideo() + coverPath);

			
			//将需要保存的信息写入video中
			Videos videos = new Videos();
			videos.setUserId(userId);
			videos.setAudioId(bgmDetail.getId());
			videos.setVideoDesc(desc);
			videos.setVideoPath(uploadPath);
			videos.setCoverPath(coverPath);
			videos.setVideoSeconds(Float.parseFloat(videoSeconds+""));
			videos.setVideoWidth(videoWidth);
			videos.setVideoHeight(videoHeight);
			videos.setStatus(VideoStatusEnums.SUCCESS.value);
			videos.setCreateTime(new Date());
			videos.setLikeCounts(0L);
			String videoId = videoService.insert(videos);
			return SuperJSONResult.ok(videoId);
		}
		return SuperJSONResult.errorMsg("上传视频失败");
	}
	
	/**
	 * 上传视频封面
	 * @throws IOException 
	 */
	@ApiOperation(value="上传视频封面", notes= "上传视频封面接口")
	@PostMapping(value = "/uploadVideoCover", headers="content-type=multipart/form-data")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="videoId", value="视频Id", required = true, 
				dataType="String", paramType="form")
				 
	})
	public SuperJSONResult uploadVideo(String userId,  String videoId,  
			@ApiParam(value="短视频封面", required=true) MultipartFile files) throws IOException {
		
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(videoId)) {
			return SuperJSONResult.errorMsg("上传用户未登录");
		}
		String fileSpace = resourceConfig.getFileVideo();
		String uploadPath = "/" + userId + "/video/";
		//数据库保存的路径
		String uploadPathDB = null;
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		//文件上传的最终路径
		String finalVideoPath = null;
		try {
			if (files != null) {
				//获取文件名
				String filename = files.getOriginalFilename();
		        String fileNamejpg = filename.substring(filename.lastIndexOf(".") + 1);
 		        //更改文件名
		        String fileNameNew = UUID.randomUUID().toString()+ "." +fileNamejpg;
		        uploadPathDB = uploadPath + fileNameNew;
				if (!StringUtils.isEmpty(filename)) {
					finalVideoPath = fileSpace + uploadPath + fileNameNew;
					File file = new File(finalVideoPath);
					if (file.getParentFile() != null && !file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					fileOutputStream = new FileOutputStream(file);
					inputStream = files.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return SuperJSONResult.errorMsg("上传视频失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return SuperJSONResult.errorMsg("上传视频失败");
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
		
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(uploadPathDB);
		videoService.updateById(video);
		
		return SuperJSONResult.ok();
	}
	
	/**
	 * 分页查询视频信息//增加搜索查询
	 * @param page
	 * @return
	 */
	@ApiOperation(value="视频分页查询", notes= "视频分页查询接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="videoId", value="视频Id", required = true, 
				dataType="String", paramType="form")
				 
	})
	@PostMapping("/queryAllVideo")
	public SuperJSONResult queryAllVideo(@RequestBody Videos video,String userId, Integer isSaveSearch, Integer page) {
		if (page == null) {
			page =1;
		}
		PagedResult queryAllVideo = videoService.queryAllVideo(video, userId, isSaveSearch, page, ConstantConfig.PAGE_SIZE);
		
		return SuperJSONResult.ok(queryAllVideo);
		
	}
	
	/**
	 * 分页查询视频信息/收藏的信息
	 * @param page
	 * @return
	 */
	@ApiOperation(value="收藏视频分页查询", notes= "收藏视频分页查询接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="page", value="第几页", required = true, 
				dataType="Integer", paramType="form")
		
	})
	@PostMapping("/collectionAllVideo")
	public SuperJSONResult collectionAllVideo(String userId, Integer page) {
		if (page == null) {
			page =1;
		}
		PagedResult queryAllVideo = videoService.collectionAllVideo(userId, page, ConstantConfig.PAGE_SIZE);
		
		return SuperJSONResult.ok(queryAllVideo);
		
	}
	
	/**
	 * 分页查询视频信息/关注的人的视频
	 * @param page
	 * @return
	 */
	@ApiOperation(value="收藏视频分页查询", notes= "收藏视频分页查询接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
				@ApiImplicitParam(name="page", value="第几页", required = true, 
				dataType="Integer", paramType="form")
		
	})
	@PostMapping("/followAllVideo")
	public SuperJSONResult followAllVideo(String userId, Integer page) {
		if (page == null) {
			page =1;
		}
		PagedResult queryAllVideo = videoService.followAllVideo(userId, page, ConstantConfig.PAGE_SIZE);
		
		return SuperJSONResult.ok(queryAllVideo);
		
	}
	
	/**
	 * 分页查询视频信息//增加搜索查询
	 * @param page
	 * @return
	 */
	@ApiOperation(value="热搜词查询", notes= "热搜词查询接口")
	@PostMapping("/hotSearch")
	public SuperJSONResult hotSearch() {
		List<String> contentList = videoService.getHotContent();
		//contentList.stream().sorted().forEachOrdered(str->syso);
		return SuperJSONResult.ok(contentList);
		
	}
	
	/**
	 * 点赞
	 * @param page
	 * @return
	 */
	@ApiOperation(value="点赞", notes= "点赞接口")
	@PostMapping("/likeVideo")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="videoId", value="视频Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="videoCreateId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
				 
	})
	public SuperJSONResult likeVideo(String userId, String videoId, String videoCreateId) {
		videoService.addVideoLikeCounts(userId, videoId, videoCreateId);
		return SuperJSONResult.ok();
		
	}
	
	/**
	 * 取消点赞
	 * @param page
	 * @return
	 */
	@ApiOperation(value="取消点赞", notes= "取消点赞接口")
	@PostMapping("/unLikeVideo")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="videoId", value="视频Id", required = true, 
				dataType="String", paramType="form"),
		@ApiImplicitParam(name="videoCreateId", value="用户Id", required = true, 
				dataType="String", paramType="form"),
				 
	})
	public SuperJSONResult unLikeVideo(String userId, String videoId, String videoCreateId) {
		videoService.reduceVideoLikeCounts(userId, videoId, videoCreateId);
		return SuperJSONResult.ok();
		
	}
}
