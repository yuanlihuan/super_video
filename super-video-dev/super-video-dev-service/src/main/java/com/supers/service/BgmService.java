package com.supers.service;

import java.util.List;

import com.supers.pojo.Bgm;



public interface BgmService {

	 /**
	  * 查询bgm音乐列表
	  * return list<bgm>
	  */
	public List<Bgm> queryBgmList();
	
	/**
	 * 根据音频id查询音频信息
	 * @param bgm
	 * @return
	 */
	public Bgm selectByBgmId(Bgm bgm);
}
