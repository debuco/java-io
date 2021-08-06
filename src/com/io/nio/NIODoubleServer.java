package com.io.nio;

public class NIODoubleServer {

    private static Integer DEFAULT_PORT = 8888;
    private static NIODoubleServerHandler serverHandle;

    public static void start() {
        start(DEFAULT_PORT);
    }

    public static synchronized void start(Integer port) {
        if (serverHandle != null) {
            serverHandle.stop();
        }
        serverHandle = new NIODoubleServerHandler(port);
        new Thread(serverHandle, "Server").start();
    }
}
