package nio.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {
    /**
     *  mainReactor監聽的socket通道
     */
    private final ServerSocketChannel ssc;
    /**
     * 取得CPU核心數
     */
    private final int cores = Runtime.getRuntime().availableProcessors();
    /**
     * 創建核心數個selector給subReactor用
     */
    private final Selector[] selectors = new Selector[cores];
    /**
     * 當前可使用的subReactor索引
     */
    private int selIdx = 0;
    /**
     * subReactor線程
     */
    private TCPSubReactor[] r = new TCPSubReactor[cores];
    /**
     * subReactor線程
     */
    private Thread[] t = new Thread[cores];

    public Acceptor(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;
        // 創建多個selector以及多個subReactor線程
        for (int i = 0; i < cores; i++) {
            selectors[i] = Selector.open();
            r[i] = new TCPSubReactor(selectors[i], ssc, i);
            t[i] = new Thread(r[i]);
            t[i].start();
        }
    }

    @Override
    public synchronized void run() {
        try {
            // 接受client連線請求
            SocketChannel sc = ssc.accept();
            System.out.println(sc.socket().getRemoteSocketAddress().toString()
                + " is connected.");

            if (sc != null) {
                // 設置為非阻塞
                sc.configureBlocking(false);
                // 暫停線程
                r[selIdx].setRestart(true);
                // 使一個阻塞住的selector操作立即返回
                selectors[selIdx].wakeup();
                // SocketChannel向selector[selIdx]註冊一個OP_READ事件，然後返回該通道的key
                SelectionKey sk = sc.register(selectors[selIdx], SelectionKey.OP_READ);
                // 使一個阻塞住的selector操作立即返回
                selectors[selIdx].wakeup();
                // 重啟線程
                r[selIdx].setRestart(false);
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