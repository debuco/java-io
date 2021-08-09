package com.io.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class ReadState implements HandlerState {

    private SelectionKey sk;

    public ReadState() {
    }

    @Override
    public void changeState(TCPHandler h) {
        // TODO Auto-generated method stub
        h.setState(new WorkState());
    }

    @Override
    public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolExecutor pool) throws IOException { // read()
        this.sk = sk;
        // non-blocking下不可用Readers，因為Readers不支援non-blocking
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);
        // 讀取字符串
        int numBytes = sc.read(buf);
        if(numBytes == -1)
        {
            System.out.println("[Warning!] A client has been closed.");
            h.closeChannel();
            return;
        }
        // 將讀取到的byte內容轉為字符串型態
        String str = new String(arr);
        if ((str != null) && !str.equals(" ")) {
            // 改變狀態(READING->WORKING)
            h.setState(new WorkState());
            // do process in worker thread
            pool.execute(new WorkerThread(h, str));
            System.out.println(sc.socket().getRemoteSocketAddress().toString()
                + " > " + str);
        }

    }

    /**
     * 執行邏輯處理之函數
     */
    synchronized void process(TCPHandler h, String str) {
        // do process(decode, logically process, encode)..
        // ..
        // 改變狀態(WORKING->SENDING)
        h.setState(new WriteState());
        // 通過key改變通道註冊的事件
        this.sk.interestOps(SelectionKey.OP_WRITE);
        // 使一個阻塞住的selector操作立即返回
        this.sk.selector().wakeup();
    }

    /**
     * 工作者線程
     */
    class WorkerThread implements Runnable {

        TCPHandler h;
        String str;

        public WorkerThread(TCPHandler h, String str) {
            this.h = h;
            this.str=str;
        }

        @Override
        public void run() {
            process(h, str);
        }

    }
}