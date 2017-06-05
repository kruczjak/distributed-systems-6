package com.company;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by kruczjak on 5/31/17.
 */
public class ZooKeeperChildWatcher implements AsyncCallback.Children2Callback {

    @Override
    public void processResult(int i, String s, Object o, List<String> list, Stat stat) {
        if (list == null) return;
        System.out.println("Change in children! Children number: " + list.size());
    }
}
