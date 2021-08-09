package com.io.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {
    /**
     *  mainReactor监听的socket通道
     */
    private final ServerSocketChannel ssc;
    /**
     * 取得CPU核心数
     */
    private final int cores = Runtime.getRuntime().availableProcessors();
    /**
     * 创建核心数个selector给subReactor用
     */
    private final Selector[] selectors = new Selector[cores];
    /**
     * 当前可使用的subReactor索引
     */
    private int selIdx = 0;
    /**
     * subReactor线程
     */
    private TCPSubReactor[] subReactors = new TCPSubReactor[cores];
    /**
     * subReactor线程
     */
    private Thread[] threads = new Thread[cores];

    public Acceptor(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;
        // 创建多个selector以及多个subReactor线程
        for (int i = 0; i < cores; i++) {
            selectors[i] = Selector.open();
            subReactors[i] = new TCPSubReactor(selectors[i], ssc, i);
            threads[i] = new Thread(subReactors[i]);
            threads[i].start();
        }
    }

    @Override
    public synchronized void run() {
        try {
            // 接受client连接請求
            SocketChannel sc = ssc.accept();
            System.out.println(sc.socket().getRemoteSocketAddress().toString()
                + " is connected.");

            if (sc != null) {
                // 設置為非阻塞
                sc.configureBlocking(false);
                // 暫停線程
                subReactors[selIdx].setRestart(true);
                // 使一個阻塞住的selector操作立即返回
                selectors[selIdx].wakeup();
                // SocketChannel向selector[selIdx]註冊一個OP_READ事件，然後返回該通道的key
                SelectionKey sk = sc.register(selectors[selIdx], SelectionKey.OP_READ);
                // 使一個阻塞住的selector操作立即返回
                selectors[selIdx].wakeup();
                // 重啟線程
                subReactors[selIdx].setRestart(false);
                // 給定key一個附加的TCPHandler對象
                sk.attach(new TCPHandler(sk, sc));
                if (++selIdx == selectors.length) {
                    selIdx = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}