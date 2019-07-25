package com.supers.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {

	private final static Logger log = LoggerFactory.getLogger(ZKCurator.class);
	private CuratorFramework client;
	
	public ZKCurator (CuratorFramework client) {
		this.client = client;
	}
	
	public void init() {
		client = client.usingNamespace("admin");
		try {
			if (client.checkExists().forPath("/bgm") == null) {
				client.create().creatingParentsIfNeeded()
				.withMode(CreateMode.PERSISTENT)
				.withACL(Ids.OPEN_ACL_UNSAFE)
				.forPath("/bgm");
				log.info("创建节点:##",client.isStarted());
			}
		} catch (Exception e) {
			log.info("创建节点失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * zookeeper新增结点
	 */
	public void addZkCurator(String bgmId, String zkNodeMap) {
		try {
			client.create().creatingParentsIfNeeded()
			.withMode(CreateMode.PERSISTENT)
			.withACL(Ids.OPEN_ACL_UNSAFE)
			.forPath("/bgm/"+ bgmId, zkNodeMap.getBytes());
			log.info("创建节点:##",client.isStarted());
 
		} catch (Exception e) {
			log.info("创建节点失败");
			e.printStackTrace();
		}
	}
}
