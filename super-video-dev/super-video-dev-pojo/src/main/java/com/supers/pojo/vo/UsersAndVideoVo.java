package com.supers.pojo.vo;

import io.swagger.annotations.ApiModel;


@ApiModel(value="用户视频点赞对象", description="这是用户视频点赞对象")
public class UsersAndVideoVo {
	
	private UsersVo usersVo;
	
	private boolean bol;

	public UsersVo getUsersVo() {
		return usersVo;
	}

	public void setUsersVo(UsersVo usersVo) {
		this.usersVo = usersVo;
	}

	public boolean isBol() {
		return bol;
	}

	public void setBol(boolean bol) {
		this.bol = bol;
	}
}