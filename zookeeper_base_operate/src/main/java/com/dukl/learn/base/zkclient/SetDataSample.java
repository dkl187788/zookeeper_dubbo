package com.dukl.learn.base.zkclient;

import org.I0Itec.zkclient.ZkClient;

public class SetDataSample {
    public static void main(String[] args) throws Exception {
    	String path = "/zk-book";
    	ZkClient zkClient = new ZkClient("127.0.0.1:2181", 2000);
        zkClient.createEphemeral(path, new Integer(1));
        zkClient.writeData(path, new Integer(1));
    }
}