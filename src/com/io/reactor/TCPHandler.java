package nio.reactor;

import nio.HandlerState;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TCPHandler implements Runnable {

    private final SelectionKey sk;
    private final SocketChannel sc;
    private static final int THREAD_COUNTING = 10;
    /**
     * 線程池
     */
    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(
        THREAD_COUNTING, THREAD_COUNTING, 10, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>());
    /**
     * 以狀態模式實現Handler
     */
    HandlerState state;

    public TCPHandler(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        // 初始狀態設定為READING
        state = new ReadState();
        // 設置線程池最大線程數
        pool.setMaximumPoolSize(32);
    }

    @Override
    public void run() {
        try {
            state.handle(this, sk, sc, pool);

        } catch (IOException e) {
            System.out.println("[Warning!] A client has been closed.");
            closeChannel();
        }
    }

    public void closeChannel() {
        try {
            sk.cancel();
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setState(HandlerState state) {
        this.state = state;
    }
}