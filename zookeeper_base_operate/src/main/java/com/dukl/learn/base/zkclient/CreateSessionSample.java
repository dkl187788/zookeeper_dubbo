package com.dukl.learn.base.zkclient;

import org.I0Itec.zkclient.ZkClient;

import java.io.IOException;

public class CreateSessionSample {
    public static void main(String[] args) throws IOException, InterruptedException {
    	ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
    	System.out.println("ZooKeeper session established.");
    }
}