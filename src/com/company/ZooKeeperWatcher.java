package com.company;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Created by kruczjak on 5/31/17.
 */
public class ZooKeeperWatcher implements Watcher, Runnable {
    private final ZooKeeper zooKeeper;
    private final ZooKeeperChildWatcher zooKeeperChildWatcher = new ZooKeeperChildWatcher();
    private final String[] programToRun;
    private Process program;

    public ZooKeeperWatcher(ZooKeeper zooKeeper, String[] programToRun) {
        this.zooKeeper = zooKeeper;
        this.programToRun = programToRun;
    }

    @Override
    public void run() {
        zooKeeper.register(this);
        this.zooKeeperExistsCall();
        while(true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        this.processEvent(watchedEvent);
        this.zooKeeperExistsCall();
        this.zooKeeperChildrenCall();
    }

    private void processEvent(WatchedEvent watchedEvent) {
        if(!Main.ZNODE.equals(watchedEvent.getPath())) return;

        switch (watchedEvent.getType()) {
            case NodeCreated:
                this.startProgram();
                break;
            case NodeDeleted:
                this.stopProgram();
                break;
            default:
                break;
        }
    }

    private void startProgram() {
        if (this.program !=  null) return;

        try {
            this.program = Runtime.getRuntime().exec(this.programToRun);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopProgram() {
        if (this.program == null) return;
        this.program.destroy();
    }

    private void zooKeeperExistsCall() {
        this.zooKeeper.exists(Main.ZNODE, true, null, this);
    }

    private void zooKeeperChildrenCall() {
        this.zooKeeper.getChildren(Main.ZNODE, true, zooKeeperChildWatcher, this);
    }
}
