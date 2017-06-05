package com.company;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static final String ZNODE = "/znode_testowy";
    private final ZooKeeper zooKeeper;
    private final Scanner scanner;
    private final String[] programCommand;

    public Main(String host, String[] programCommand) throws IOException {
        this.programCommand = programCommand;
        this.zooKeeper = new ZooKeeper(host, 10000, null);
        this.scanner = new Scanner(System.in);
    }

    public void startWatcher() throws KeeperException, InterruptedException {
        Thread watcher = new Thread(new ZooKeeperWatcher(zooKeeper, this.programCommand));
        watcher.start();

        while(true) {
            String input = scanner.next();

            if (input.startsWith("list")) {
                printNodeTree(null, 0);
            } else if (input.startsWith("quit")) {
                watcher.interrupt();
                break;
            }
        }
    }

    private void printNodeTree(String znodeName, int indentSize) throws KeeperException, InterruptedException {
        String znode;
        if (znodeName != null) {
            znode = znodeName;
        } else {
            znode = ZNODE;
        }
        for (int i = 0; i < indentSize; i++) System.out.print(" ");
        System.out.println(znode);

        try {
            for (String childName : zooKeeper.getChildren(znode, false)) {
                printNodeTree(znode + "/" + childName, indentSize + 1);
            }
        } catch (KeeperException.NoNodeException e) {
            System.out.println("Not exists");
        }
    }

    public static void main(String[] args) {
        String host = args[0];
        String programCommand[] = Arrays.copyOfRange(args, 1, args.length);

        try {
            Main main = new Main(host, programCommand);
            main.startWatcher();
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
