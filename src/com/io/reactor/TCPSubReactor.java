package com.io.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPSubReactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;
    private boolean restart = false;
    int num;

    public TCPSubReactor(Selector selector, ServerSocketChannel ssc, int num) {
        this.ssc = ssc;
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        // 在线程被中断前持续运行
        while (!Thread.interrupted()) {
            //System.out.println("ID:" + num
            //		+ " subReactor waiting for new event on port: "
            //		+ ssc.socket().getLocalPort() + "...");
            System.out.println("waiting for restart");
            // 在线程被中断前以及被指定重启前持续运行
            while (!Thread.interrupted() && !restart) {
                try {
                    if (selector.select() == 0)
                    {
                        // 若沒有事件就绪则不往下执行
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
    }

    /**
     * name: dispatch(SelectionKey key) description: 调度方法，根据事件绑定的对象开新线程
     */
    private void dispatch(SelectionKey key) {
        // 根据事件之key绑定的对象开新线程
        Runnable r = (Runnable) (key.attachment());
        if (r != null) {
            r.run();
        }
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}