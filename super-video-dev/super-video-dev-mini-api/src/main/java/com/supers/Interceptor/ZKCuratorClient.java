package com.supers.Interceptor;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.supers.conf.ResourceConfig;
import com.supers.enums.BGMOperatorTypeEnum;
import com.supers.pojo.Bgm;
import com.supers.service.BgmService;
import com.supers.utils.ConstantConfig;
import com.supers.utils.JsonUtils;

/**
 * zookeeper的客户端
 * @author liulai
 *
 */
public class ZKCuratorClient {
	
	@Autowired
	private BgmService bgmService;
	
	@Autowired
	private ResourceConfig resourceConfig;
	
	private final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);
	
	private CuratorFramework client;
	
	//private final static String zkIp = "106.13.124.69:2181";
	//private final static String zkIp = "192.168.101.128:2181";
	 
	public void init() {
		//设置重连策略
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
		if (client != null) {
			return;
		}
		//创建zk客户端
		client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer()).sessionTimeoutMs(10000)
				.retryPolicy(retryPolicy).namespace("admin").build();
		//启动客户端
		client.start();
		
		//测试获取结点
		try {
			addChildWatch("/bgm");
			/*System.out.println("============================");
			String  path = new String(client.getData().forPath("/bgm/190331BTNC42K968"));
			log.info(path);
			System.out.println(path+"--------------------");*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addChildWatch(String nodePath) throws Exception {
		final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
		cache.start();
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
					throws Exception {
				if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
					log.info("监听到zookeeper事件");
					//下载mp3歌曲
					String path = event.getData().getPath();
					//String operatorType = new String(event.getData().getPath());
					String operatorObjStr = new String(event.getData().getData());
					Map<String, String> zkNodeMap = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
					String type = zkNodeMap.get("type");//type = 1 下载 2 删除
					String songPath = zkNodeMap.get("bgmPath");
					System.out.println(type+"--------------------"+songPath);
					/*String arr[] = path.split("/");
					String bgmId = arr[arr.length - 1];
					Bgm bgm = new Bgm();
					bgm.setId(bgmId);
					Bgm bgms = bgmService.selectByBgmId(bgm);
					if (bgms == null) {
						return;
					}
					//获取Bgm所在的相对路径
					String songPath = bgm.getPath();*/
					
					//定义保存本地的bgm路径
					//String filePath = ConstantConfig.USER_IMAGE_VIDEO +"/face/"+ songPath;
					String filePath = resourceConfig.getFileBgm() +"/face/"+ songPath;
					//d定义下载的路径（播放url）
					String arrPath[] = songPath.split("\\\\");
					String finalPath = "";
					for(int i = 0; i < arrPath.length ; i ++) {
						if (StringUtils.isNotBlank(arrPath[i])) {
							finalPath += "/";
							finalPath += URLEncoder.encode(arrPath[i], "UTF-8") ;
						}
					}
					//如果路径有中文可以使用utf-8编码
					//String bgmUrl = ConstantConfig.BGM_IP_NODE + URLEncoder.encode(songPath, "UTF-8");
					String bgmUrl = resourceConfig.getBgmServer() + URLEncoder.encode(songPath, "UTF-8");
					//http://172.16.7.43:8080/mvc/bgm/6af9efb3-b7db-4fde-872a-77d6daf74f25.mp3
					if (type.equals(BGMOperatorTypeEnum.ADD.getUserType())) {
						// 下载bgm到spingboot服务器
						URL url = new URL(bgmUrl);
						File file = new File(filePath);
						FileUtils.copyURLToFile(url, file);
						client.delete().forPath(path);
					} else if (type.equals(BGMOperatorTypeEnum.DELETE.getUserType())) {
						File file = new File(filePath);
						FileUtils.forceDelete(file);
						client.delete().forPath(path);
					}
				}
			}
		});
	}
}
