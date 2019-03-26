package com.supers.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.supers.mapper.BgmMapper;
import com.supers.pojo.Bgm;
import com.supers.service.BgmService;

@Service
public class BgmServiceImpl implements BgmService {

	@Autowired
	private BgmMapper bgmMapper;

	@Override
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<Bgm> queryBgmList() {

		return bgmMapper.selectAll();
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public Bgm selectByBgmId(Bgm bgm) {
		
 		return bgmMapper.selectOne(bgm);
	}

	
}
