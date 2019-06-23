package com.dukl.learn.function.distributed_lock;

import com.dukl.learn.utils.FastJsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dukangli on 2019/4/15 17:55
 */
public class LockWatcher implements Watcher {
    private Logger logger = LogManager.getLogger(this.getClass());

    /**
     * 成员变量
     **/
    private ZooKeeper zk = null;
    // 当前业务线程竞争锁的时候创建的节点路径
    private String selfPath = null;
    // 当前业务线程竞争锁的时候创建节点的前置节点路径
    private String waitPath = null;
    // 确保连接zk成功；只有当收到Watcher的监听事件之后，才执行后续的操作，否则请求阻塞在createConnection()创建ZK连接的方法中
    private CountDownLatch connectSuccessLatch = new CountDownLatch(1);
    // 标识线程是否执行完任务
    private CountDownLatch threadCompleteLatch = null;

    /**
     * ZK的相关配置常量
     **/
    private static final String LOCK_ROOT_PATH = "/exclusive_lock";

    private static final String LOCK_SUB_PATH = LOCK_ROOT_PATH + "/lock";

    public LockWatcher(CountDownLatch latch) {
        this.threadCompleteLatch = latch;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        // 通知状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();
        logger.info(FastJsonUtil.toJSONString(event));
        // 根据通知状态分别处理
        if (Event.KeeperState.SyncConnected == keeperState) {
            if (Event.EventType.None == eventType) {
                logger.info(Thread.currentThread().getName() + " 成功连接上zk服务器");
                // 此处代码的主要作用是用来辅助判断当前线程确实已经连接上ZK
                connectSuccessLatch.countDown();
            } else if (event.getType() == Event.EventType.NodeDeleted) {
                logger.info(Thread.currentThread().getName() + " 节点" + event.getPath() + "已删除成功");
                if (event.getPath().equals(waitPath)) {
                    logger.info(Thread.currentThread().getName() + " 收到情报，排我前面的兄弟已挂，我准备再次确认该不该我出场了？");
                    try {
                        if (checkMinPath()) {
                            getLockSuccess();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (event.getType() == Event.EventType.NodeCreated) {
                logger.info(Thread.currentThread().getName() + "节点创建成功");
            } else if (event.getType() == Event.EventType.NodeDataChanged) {
                logger.info(Thread.currentThread().getName() + "节点数据被修改");
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                logger.info(Thread.currentThread().getName() + "子节点更新");
            }
        } else if (Event.KeeperState.Disconnected == keeperState) {
            logger.info(Thread.currentThread().getName() + "与zk服务器断开连接");
        } else if (Event.KeeperState.AuthFailed == keeperState) {
            logger.info(Thread.currentThread().getName() + "权限检查失败");
        } else if (Event.KeeperState.Expired == keeperState) {
            logger.info(Thread.currentThread().getName() + "会话失效");
        }
    }

    /**
     * 创建ZK连接
     */
    public void createConnection(String connectString, int sessionTimeout) throws IOException, InterruptedException {
        zk = new ZooKeeper(connectString, sessionTimeout, this);
        // connectSuccessLatch.await(1, TimeUnit.SECONDS) 正式实现的时候可以考虑此处是否采用超时阻塞
        connectSuccessLatch.await();
    }

    /**
     * 创建ZK节点
     */
    public boolean createPersistentPath(String path, String data, boolean needWatch) throws KeeperException, InterruptedException {
        if (zk.exists(path, needWatch) == null) {
            String result = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            logger.info(Thread.currentThread().getName() + "创建节点成功, path: " + result + ", content: " + data);
        }
        return true;
    }

    /**
     * 获取分布式锁
     */
    public void getLock() throws Exception {
        // 创建临时的顺序节点
        selfPath = zk.create(LOCK_SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.info(Thread.currentThread().getName() + "创建锁路径:" + selfPath);
        // 判断是否自己目前是最小的序列号 如果是标识自己获取锁成功，否则 需要记录自己前面的序列号是多少
        if (checkMinPath()) {
            getLockSuccess();
        }
    }

    /**
     * 获取锁成功
     */
    private void getLockSuccess() throws KeeperException, InterruptedException {
        if (zk.exists(selfPath, false) == null) {
            System.err.println(Thread.currentThread().getName() + "本节点已丢失了...");
            return;
        }
        logger.info(Thread.currentThread().getName() + "获取锁成功，开始处理业务数据！");
        /**
         * 具体的业务处理
         */
        Thread.sleep(2000);
        logger.info(Thread.currentThread().getName() + "处理业务数据完成，删除本节点：" + selfPath);
        zk.delete(selfPath, -1);
        // 是否连接后，会话结束 临时节点会删除 其他的客户端线程会收到通知
        releaseConnection();
        threadCompleteLatch.countDown();
    }

    /**
     * 关闭ZK连接
     */
    private void releaseConnection() {
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info(Thread.currentThread().getName() + "释放zookeeper连接");
    }

    /**
     * 检查自己是不是最小的节点
     */
    private boolean checkMinPath() throws Exception {
        List<String> subNodes = zk.getChildren(LOCK_ROOT_PATH, false);
        logger.info(FastJsonUtil.toJSONString(subNodes));
        // 根据元素按字典序升序排序
        Collections.sort(subNodes);
        logger.info(Thread.currentThread().getName() + "创建的临时节点名称:" + selfPath.substring(LOCK_ROOT_PATH.length() + 1));
        int index = subNodes.indexOf(selfPath.substring(LOCK_ROOT_PATH.length() + 1));
        logger.info(Thread.currentThread().getName() + "创建的临时节点的index:" + index);
        switch (index) {
            case -1: {
                System.err.println(Thread.currentThread().getName() + "创建的节点已不在了..." + selfPath);
                return false;
            }
            case 0: {
                logger.info(Thread.currentThread().getName() + "子节点中，目前排序我是最靠前的" + selfPath);
                return true;
            }
            default: {
                // 获取比当前节点小的前置节点,此处只关注前置节点是否还在存在，避免惊群现象产生
                waitPath = LOCK_ROOT_PATH + "/" + subNodes.get(index - 1);
                logger.info(Thread.currentThread().getName() + "获取子节点中，排在我前面的节点是:" + waitPath);
                try {
                    zk.getData(waitPath, true, new Stat());
                    return false;
                } catch (Exception e) {
                    if (zk.exists(waitPath, false) == null) {
                        logger.info(Thread.currentThread().getName() + "子节点中，排在我前面的" + waitPath + "已失踪，该我了");
                        return checkMinPath();
                    } else {
                        throw e;
                    }
                }
            }

        }
    }
}
