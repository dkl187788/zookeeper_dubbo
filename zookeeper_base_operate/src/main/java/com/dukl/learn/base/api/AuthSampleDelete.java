package com.dukl.learn.base.api;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class AuthSampleDelete implements Watcher {
    final static String PATH = "/zk-book-auth_test";

    final static String PATH2 = "/zk-book-auth_test/child";

    final static String PATH3 = "/zk-book-auth_test/child2";

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        AuthSampleDelete watcher = new AuthSampleDelete();
        ZooKeeper zookeeper1 = new ZooKeeper("127.0.0.1:2181", 5000, watcher);
        zookeeper1.addAuthInfo("digest", "foo:true".getBytes());
        //zookeeper1.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);

        zookeeper1.create(PATH2, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

        //zookeeper1.create(PATH3, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);

        try {
            ZooKeeper zookeeper2 = new ZooKeeper("127.0.0.1:2181", 5000, watcher);
            zookeeper2.delete(PATH2, -1);
        } catch (Exception e) {
            System.out.println("fail to delete: " + e.getMessage());
        }

        ZooKeeper zookeeper3 = new ZooKeeper("127.0.0.1:2181", 5000, watcher);
        zookeeper3.addAuthInfo("digest", "foo:true".getBytes());
        zookeeper3.delete(PATH2, -1);
        System.out.println("success delete znode: " + PATH2);

        ZooKeeper zookeeper4 = new ZooKeeper("127.00.1:2181", 5000, watcher);
        zookeeper4.addAuthInfo("digest", "foo:true".getBytes());
        zookeeper4.delete(PATH3,-1);
        zookeeper4.delete(PATH, -1);
        System.out.println("success delete znode: " + PATH);
    }

    public void process(WatchedEvent event) {
        try {
            if (KeeperState.SyncConnected == event.getState()) {
                if (EventType.None == event.getType() && null == event.getPath()) {
                    connectedSemaphore.countDown();
                } else if (EventType.NodeCreated == event.getType()) {
                    System.out.println("success create znode : " + event.getPath());
                } else if (EventType.NodeDeleted == event.getType()) {
                    System.out.println("success delete znode : " + event.getPath());
                } else if (Event.EventType.NodeDataChanged == event.getType()) {
                    System.out.println("data changed of znode : " + event.getPath());
                } else if (event.getType() == EventType.NodeChildrenChanged) {
                    try {
                        System.out.println("ReGet Child:" + event.getPath());
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
