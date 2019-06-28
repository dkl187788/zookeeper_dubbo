package com.dukl.learn.base.zkclient;
import org.I0Itec.zkclient.ZkClient;

public class CreateNodeSample {
    public static void main(String[] args) throws Exception {
    	ZkClient zkClient = new ZkClient("127.0.0.1:2182", 5000);
        String path = "/dukl_test";
        zkClient.createPersistent(path, true);
        zkClient.delete(path);
        //zkClient.createEphemeral(path+"/c3","123456");
        //System.out.println("success create znode.");
        //TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }
}