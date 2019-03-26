package com.supers.service.impl;

import java.util.Date;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supers.mapper.UsersReportMapper;
import com.supers.pojo.UsersReport;
import com.supers.service.UserReportService;

@Service
public class UserReportServiceImpl implements UserReportService {

	@Autowired
	private UsersReportMapper usersReportMapper;
	
	@Autowired
	private Sid sid;
	
	@Override
	public void insertUserReportVideo(UsersReport userReport) {
		userReport.setId(sid.nextShort());
		//TODO
		userReport.setCreateDate(new Date());
		usersReportMapper.insert(userReport);
	}

	
	
	
}
