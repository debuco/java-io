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
        // 在线程被中断前运行
        while (!Thread.interrupted()) {
            System.out.println("mainReactor waiting for new event on port: "
                + ssc.socket().getLocalPort() + "...");
            try {
                // 若沒有事件就绪，则不往下执行
                if (selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取得所有已就绪事件的key集合
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while (it.hasNext()) {
                // 根据事件的key进行调度
                dispatch((SelectionKey) (it.next()));
                it.remove();
            }
        }
    }

    /**
     * name: dispatch(SelectionKey key)
     * description: 调度方法，根据事件绑定的对象开启线程
     */
    private void dispatch(SelectionKey key) {
        // 根据事件之key绑定的对象开新线程
        Runnable r = (Runnable) (key.attachment());
        if (r != null) {
            r.run();
        }
    }

}