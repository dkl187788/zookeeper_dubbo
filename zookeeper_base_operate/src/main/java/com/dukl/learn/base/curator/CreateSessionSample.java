package com.dukl.learn.base.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CreateSessionSample {
	public static void main(String[] args) throws Exception {
		//Curator 馆长; 监护人; 管理者
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 5000, 3000, retryPolicy);
		client.start();
		System.out.println("Zookeeper session1 established. ");
		CuratorFramework client1 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
				.sessionTimeoutMs(5000).retryPolicy(retryPolicy).namespace("base").build();
		client1.start();
		System.out.println("Zookeeper session2 established. ");		
	}
}