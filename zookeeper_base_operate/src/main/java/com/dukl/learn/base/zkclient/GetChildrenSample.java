package com.dukl.learn.base.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

public class GetChildrenSample {

	public static void main(String[] args) throws Exception {
		String path = "/zk-book2";
		ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
		zkClient.subscribeChildChanges(path, new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
			}
		});

		zkClient.createPersistent(path);
		Thread.sleep(1000);
		zkClient.createPersistent(path + "/c2");
		Thread.sleep(1000);
		zkClient.delete(path + "/c2");
		Thread.sleep(1000);
		zkClient.delete(path);
		Thread.sleep(Integer.MAX_VALUE);
	}
}