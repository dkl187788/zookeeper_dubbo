package com.dukl.learn.base.zkclient;

import org.I0Itec.zkclient.ZkClient;

public class DelDataSample {
	public static void main(String[] args) throws Exception {
		String path = "/zk-book";
    	ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        zkClient.createPersistent(path, "");
        zkClient.createPersistent(path+"/c1", "");
        zkClient.deleteRecursive(path);
        System.out.println("success delete znode.");
    }
}