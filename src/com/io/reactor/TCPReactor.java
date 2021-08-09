package com.io.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPReactor implements Runnable {

    private final ServerSocketChannel ssc;
    /**
     *  mainReactor用的selector
     */
    private final Selector selector;

    public TCPReactor(int port) throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        InetSocketAddress addr = new InetSocketAddress(port);
        // 在ServerSocketChannel绑定监听端口
        ssc.socket().bind(addr);
        // 设置ServerSocketChannel为非阻塞
        ssc.configureBlocking(false);
        // ServerSocketChannel向selector注册一个OP_ACCEPT事件，然后返回该通道的key
        SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 给定key一个附加的Acceptor对象
        sk.attach(new Acceptor(ssc));
    }

    @Override
    public void run() {
        // 在線程被中斷前持續運行
        while (!Thread.interrupted()) {
            System.out.println("mainReactor waiting for new event on port: "
                + ssc.socket().getLocalPort() + "...");
            try {
                // 若沒有事件就緒則不往下執行
                if (selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取得所有已就緒事件的key集合
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while (it.hasNext()) {
                // 根據事件的key進行調度
                dispatch((SelectionKey) (it.next()));
                it.remove();
            }
        }
    }

    /*
     * name: dispatch(SelectionKey key)
     * description: 調度方法，根據事件綁定的對象開新線程
     */
    private void dispatch(SelectionKey key) {
        // 根據事件之key綁定的對象開新線程
        Runnable r = (Runnable) (key.attachment());
        if (r != null) {
            r.run();
        }
    }

}