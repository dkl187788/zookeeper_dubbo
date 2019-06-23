package com.dukl.zookeeper.base.tools;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by adu on 2019/6/22.
 */
public class ZkClientUtils {

    private static String CONNECTION_ADDRESS_STR = "127.0.0.1:2181";

    private static int CONNECTION_TIMEOUT = 5000;

    public static ZkClient createSession(String connectionAddressStr, int connectionTimeout) {
        ZkClient zkClient = new ZkClient(connectionAddressStr, connectionTimeout);
        System.out.println("ZooKeeper session established.");
        return zkClient;
    }

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = createSession(CONNECTION_ADDRESS_STR, CONNECTION_TIMEOUT);
        String initPath = "/zk-book3/c1";
        zkClient.delete(initPath + "/c3");
        zkClient.delete(initPath);

        // 创建持计划节点
        zkClient.createPersistent(initPath, true);
        // 创建临时节点
        zkClient.createEphemeral(initPath + "/c3", "123456");
        // 创建临时的顺序节点
        zkClient.createEphemeralSequential(initPath +"/childNode", "123456");
        // 子节点
        zkClient.subscribeChildChanges(initPath, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
            }
        });
        zkClient.createEphemeralSequential(initPath +"/childNode", "123456");
        // 应该把子节点也应该删除
        //zkClient.delete(initPath + "/childNode");

        zkClient.createEphemeral(initPath + "/childData", "123");
        zkClient.subscribeDataChanges(initPath + "/childData", new IZkDataListener() {
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("Node " + dataPath + " deleted.");
            }

            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Node " + dataPath + " changed, new data: " + data);
            }
        });
        zkClient.writeData(initPath + "/childData", "456");
        Thread.sleep(1000);
        zkClient.delete(initPath + "/childData");
        //Thread.sleep(Integer.MAX_VALUE);
    }
}
