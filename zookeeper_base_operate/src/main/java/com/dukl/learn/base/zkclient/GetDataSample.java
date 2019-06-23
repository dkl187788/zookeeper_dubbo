package com.dukl.learn.base.zkclient;


import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

public class GetDataSample {
	public static void main(String[] args) throws Exception {
		String path = "/zk-book";
		ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
		zkClient.createEphemeral(path, "123");

		zkClient.subscribeDataChanges(path, new IZkDataListener() {
			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("Node " + dataPath + " deleted.");
			}

			public void handleDataChange(String dataPath, Object data) throws Exception {
				System.out.println("Node " + dataPath + " changed, new data: " + data);
			}
		});

		zkClient.writeData(path, "456");
		Thread.sleep(1000);
		zkClient.delete(path);
		Thread.sleep(Integer.MAX_VALUE);
	}
}