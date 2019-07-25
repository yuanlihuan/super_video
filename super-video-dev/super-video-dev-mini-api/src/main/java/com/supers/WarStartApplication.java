package com.supers;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 相当于使用web。xml的形式启动部署
 * @author liulai
 *
 */
public class WarStartApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder builder) {
		 //使用web.xml运行应用程序，指向applicaiton最后启动
		return builder.sources(Application.class);
	}
	
}
