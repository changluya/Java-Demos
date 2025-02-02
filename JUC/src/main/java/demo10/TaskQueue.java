package demo10;

import java.util.concurrent.*;

public class TaskQueue<R extends Runnable> extends LinkedBlockingQueue<Runnable> {

    private transient ThreadPoolExecutor parent;

    public TaskQueue(int capacity) {
        super(capacity);
    }

    public void setExecutor(ThreadPoolExecutor parent) {
        this.parent = parent;
    }

    /**
     * 核心线程 -> 最大核心线程数 -> 队列
     * @param runnable the element to add
     * @return
     */
    @Override
    public boolean offer(Runnable runnable) {
        // 如果没有线程池父类，则直接尝试入队
        if (parent == null) return super.offer(runnable);
        // 若是工作线程数 < 最大线程数，则优先创建线程跑任务
        if (parent.getPoolSize() < parent.getMaximumPoolSize()) return false;
        // 工作线程数 >= 最大线程数，入队
        return super.offer(runnable);
    }
}