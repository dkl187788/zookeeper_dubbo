package com.dukl.learn.base.zkclient;

import org.I0Itec.zkclient.ZkClient;

public class ExistNodeSample {
	public static void main(String[] args) throws Exception {
		String path = "/zk-book";
		ZkClient zkClient = new ZkClient("127.0.0.1:2181", 2000);
		System.out.println("Node " + path + " exists " + zkClient.exists(path));
	}
}