package com.dukl.learn.base.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class SetDataSample {
	public static void main(String[] args) throws Exception {
		String path = "/zk-book";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
		Stat stat = new Stat();
		client.getData().storingStatIn(stat).forPath(path);
		System.out.println("Success set node for : " + path + ", new version: "
				+ client.setData().withVersion(stat.getVersion()).forPath(path).getVersion());
		try {
			client.setData().withVersion(stat.getVersion()).forPath(path);
		} catch (Exception e) {
			System.out.println("Fail set node due to " + e.getMessage());
		}
	}
}