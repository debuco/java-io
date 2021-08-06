package com.io.nio;

import java.util.Scanner;

public class NIODoubleMain {

    public static void main(String[] args) throws Exception {
        // 启动服务器  
        NIODoubleServer.start();
        Thread.sleep(100);
        // 启动客户端   
        NIODoubleClient.start();
        while (NIODoubleClient.sendMsg(new Scanner(System.in).nextLine())) {
            ;
        }
    }

}
