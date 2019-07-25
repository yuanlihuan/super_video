package com.supers.service;

import org.springframework.stereotype.Service;

import com.supers.pojo.Bgm;
import com.supers.utils.PagedResult;
@Service
public interface BgmService {

	/**
	 * 保存上传音乐的路径
	 */
	public void addBgm(Bgm bgm);

	/**
	 * 分页查询bgm列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult queryBgmList(Integer page, Integer pageSize);

	/**
	 * 删除音乐
	 */
	public void delBgmById(String bgmId);
}
