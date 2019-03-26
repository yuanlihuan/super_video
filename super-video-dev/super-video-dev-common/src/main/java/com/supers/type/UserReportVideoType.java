package com.supers.type;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public interface UserReportVideoType {

	public static final Integer aaa = 0;
	public static final Integer bbb = 1;
	
	 public static final Map<Integer, String> USER_REPORT_VIDEO_TYPE_MAP = ImmutableMap
			    .<Integer, String>of(
			    		aaa, "色情", 
			    		bbb, "低俗");
}
