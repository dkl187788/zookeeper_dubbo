package com.dukl.learn.base.zkclient;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

public class CreateNodeSample {
    public static void main(String[] args) throws Exception {
    	ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        String path = "/zk-book3/c1";
        zkClient.createPersistent(path, true);
        zkClient.createEphemeral(path+"/c3","123456");
        System.out.println("success create znode.");
        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }
}