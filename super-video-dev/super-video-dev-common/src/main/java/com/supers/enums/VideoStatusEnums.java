package com.supers.enums;

public enum VideoStatusEnums {

	SUCCESS(1), //成功
	FORBID(2);//失败管理员设置
	
	public final int value;
	
	VideoStatusEnums(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
