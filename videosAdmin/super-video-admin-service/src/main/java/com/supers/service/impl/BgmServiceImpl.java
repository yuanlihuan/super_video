package com.supers.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.supers.enums.BGMOperatorTypeEnum;
import com.supers.mapper.BgmMapper;
import com.supers.pojo.Bgm;
import com.supers.pojo.BgmExample;
import com.supers.service.BgmService;
import com.supers.utils.JsonUtils;
import com.supers.utils.PagedResult;
import com.supers.utils.ZKCurator;

@Service
public class BgmServiceImpl implements BgmService {

	@Autowired
	private BgmMapper bgmMapper;
	
	@Autowired
	private Sid sid;

	@Autowired
	private ZKCurator zKCurator;
	
	@Override
	public void addBgm(Bgm bgm) {
		bgm.setId(sid.nextShort());
		bgmMapper.insert(bgm);
		//将bgmpath存入zk节点中
		Map<String, String> zkNodeMap = new HashMap<String, String>();
		zkNodeMap.put("type", BGMOperatorTypeEnum.ADD.getUserType());
		zkNodeMap.put("bgmPath", bgm.getPath());
		zKCurator.addZkCurator(bgm.getId(), JsonUtils.objectToJson(zkNodeMap));
	}

	@Override
	public PagedResult queryBgmList(Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		BgmExample example = new BgmExample();
		List<Bgm> list = bgmMapper.selectByExample(example);
		PageInfo<Bgm> pageInfo = new PageInfo<Bgm>(list);
		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageInfo.getPages());
		pagedResult.setPage(page);
		pagedResult.setRows(list);
		pagedResult.setRecords(pageInfo.getTotal());
		return pagedResult;
	}

	@Override
	public void delBgmById(String bgmId) {
		Bgm bgm = bgmMapper.selectByPrimaryKey(bgmId);
		bgmMapper.deleteByPrimaryKey(bgmId);
		Map<String, String> zkNodeMap = new HashMap<String, String>();
		zkNodeMap.put("type", BGMOperatorTypeEnum.DELETE.getUserType());
		zkNodeMap.put("bgmPath", bgm.getPath());
		zKCurator.addZkCurator(bgmId, JsonUtils.objectToJson(zkNodeMap));
	}

}
