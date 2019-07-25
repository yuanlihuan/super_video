package com.supers.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 资源文件的配置管理 resource.properties
 * @author liulai
 *
 */
@Configuration
@ConfigurationProperties(prefix="com.super")
@PropertySource("classpath:resource-prod.properties")
public class ResourceConfig {
	
	private String zookeeperServer;
	
	private String bgmServer;
	
	private String fileFace;
	
	private String fileVideo;
	
	private String fileBgm;
	
	private String ffmpeg;
	
	public String getZookeeperServer() {
		return zookeeperServer;
	}
	public void setZookeeperServer(String zookeeperServer) {
		this.zookeeperServer = zookeeperServer;
	}
	public String getBgmServer() {
		return bgmServer;
	}
	public void setBgmServer(String bgmServer) {
		this.bgmServer = bgmServer;
	}
	public String getFileFace() {
		return fileFace;
	}
	public void setFileFace(String fileFace) {
		this.fileFace = fileFace;
	}
	public String getFileVideo() {
		return fileVideo;
	}
	public void setFileVideo(String fileVideo) {
		this.fileVideo = fileVideo;
	}
	public String getFileBgm() {
		return fileBgm;
	}
	public void setFileBgm(String fileBgm) {
		this.fileBgm = fileBgm;
	}
	public String getFfmpeg() {
		return ffmpeg;
	}
	public void setFfmpeg(String ffmpeg) {
		this.ffmpeg = ffmpeg;
	}
	
}
