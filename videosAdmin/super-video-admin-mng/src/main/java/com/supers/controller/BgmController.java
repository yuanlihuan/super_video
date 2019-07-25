package com.supers.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.supers.pojo.Bgm;
import com.supers.service.BgmService;
import com.supers.utils.PagedResult;
import com.supers.utils.SuperJSONResult;
import com.supers.utils.ZKCurator;

@Controller
@RequestMapping("/bgm")
public class BgmController {
	
	@Autowired
	private BgmService bgmService;
	
	@GetMapping("/showAddBgm")
	public String login() {
		return "/video/addBgm";
	}
	
	@GetMapping("/showBgmList")
	public String showBgmList() {
		return "/video/bgmList";
	}
	
	/**
	 * 删除音乐
	 * @param bgm
	 * @return
	 */
	@PostMapping("/delBgm")
	@ResponseBody
	public SuperJSONResult delBgm(String bgmId) {
		if (StringUtils.isBlank(bgmId)) {
			return SuperJSONResult.errorMsg("请选择删除的音乐");
		}
		bgmService.delBgmById(bgmId);
		return SuperJSONResult.ok();
	}
	
	/**
	 * 将音乐路径保存到数据库上
	 * @param bgm
	 * @return
	 */
	@PostMapping("/queryBgmList")
	@ResponseBody
	public PagedResult queryBgmList(Integer page, Integer pageSize) {
		if (StringUtils.isBlank(page+"")) {
			page = 1;
		}
		if (pageSize == null || pageSize.equals("")) {
			pageSize = 10;
		}
		
		return bgmService.queryBgmList(page, pageSize);
	}
	
	/**
	 * 将音乐路径保存到数据库上
	 * @param bgm
	 * @return
	 */
	@PostMapping("/addBgm")
	@ResponseBody
	public SuperJSONResult addBgm(Bgm bgm) {
		bgmService.addBgm(bgm);
		return SuperJSONResult.ok("保存成功");
	}
	
	
	/**
	 * 上传视频文件
	 * @param files
	 * @return
	 * @throws FileNotFoundException
	 */
	@PostMapping("/bgmUpload")
	@ResponseBody
	public SuperJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws FileNotFoundException {
		
		String fileSpace = "D:/muke";
		String uploadPath = "/bgm/";
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
		return SuperJSONResult.ok(uploadPathDb);
	}
}
