package com.changlu.powerjob;

import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class HashedWheelTimer implements Timer {

    // 每个时间格的间隔，单位毫秒
    private final long tickDuration;

    // 时间轮数组，每个元素是一个时间格
    private final HashedWheelBucket[] wheel;

    // 用于取模运算的掩码，wheel.length - 1，用于快速计算索引
    private final int mask;

//    private final Indi

    // 时间轮的启动时间
    private final long startTime;

    // 等待加入时间轮的任务队列
    private final Queue<HashedWheelTimerFuture> waitingTasks = Queues.newLinkedBlockingDeque();
    // 已取消的任务
    private final Queue<HashedWheelTimerFuture> canceledTasks = Queues.newLinkedBlockingDeque();

    @Override
    public TimeFuture schedule(TimerTask task, long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public Set<TimerTask> stop() {
        return Collections.emptySet();
    }


    /**
     * 包装 TimerTask，维护预期执行时间、总圈数等数据
     */
    private final class HashedWheelTimerFuture implements TimeFuture {

        // 预期执行时间
        private final long targetTime;
        private final TimerTask timerTask;

        // 所属的时间格，用于快速删除该任务
        private HashedWheelBucket bucket;
        // 总圈数
        private long totalTicks;
        // 当前状态 0-初始化等待中、1-运行中、2-完成，3-已取消
        private int status;

        // 枚举状态值
        private static final int WAITING = 0;
        private static final int RUNNING = 1;
        private static final int FINISHED = 2;
        private static final int CANCELED = 3;

        /**
         * 初始化包装时间轮任务
         * @param timerTask
         * @param targetTime
         */
        public HashedWheelTimerFuture(TimerTask timerTask, long targetTime) {
            this.targetTime = targetTime;
            this.timerTask = timerTask;
            this.status = WAITING;
        }

        @Override
        public TimerTask getTask() {
            return timerTask;
        }

        @Override
        public boolean cancel() {
            if (status == WAITING) {
                status = CANCELED;
                canceledTasks.add(this);
                return true;
            }
            return false;
        }

        @Override
        public boolean isCancelled() {
            return status == CANCELED;
        }

        @Override
        public boolean isDone() {
            return status == FINISHED;
        }
    }

    /**
     * 时间轮 格子
     */
    private final class HashedWheelBucket extends LinkedList<HashedWheelTimerFuture> {

        public void expireTimerTasks(long currentTick) {
            removeIf(timerFuture -> {
                // 如果任务已被取消，直接移除
                if (timerFuture.status == HashedWheelTimerFuture.CANCELED) {
                    return true;
                }

                // 如果任务状态不是等待中，记录警告日志
                if (timerFuture.status != HashedWheelTimerFuture.WAITING) {
//                    log.("[HashedWheelTimer] impossible, please fix the bug");

                    return true;
                }

            });
        }

    }

    private class Indicator implements Runnable {

        // 当前tick数
        private long tick = 0;

        // 停止标志
        private final AtomicBoolean stop = new AtomicBoolean(false);

        // 用于等待指针线程结束的CountDownLatch
        private final CountDownLatch latch = new CountDownLatch(1);


        @Override
        public void run() {
            while (!stop.get()) {
                // 1. 将任务从队列推入时间轮
                pushTaskToBucket();
                // 2、处理取消的任务
                processCanceledTasks();
                // 3、等待指针跳向下一刻
                tickTack();
                // 4、执行定时任务
                int currentIndex = (int)(tick & mask);
                HashedWheelBucket bucket = wheel[currentIndex];
                bucket.expireTimerTasks(tick);
                // tick自增
                tick ++;
            }
            // 指针线程结束，释放CountDownLatch
            latch.countDown();
        }

        /**
         * 模拟指针转动，当返回时指针已经转到了下一个刻度
         */
        private void tickTack() {
            // 计算下一次调度的绝对时间
            long nextTime = startTime + (tick + 1) * tickDuration;
            // 计算需要睡眠的时间
            long sleepTime = nextTime - System.currentTimeMillis();
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                }catch (Exception ignore) {
                }
            }
        }

        /**
         * 将队列中的任务推入时间轮中
         */
        private void pushTaskToBucket() {
            while (true) {
                // 阻塞取出等待任务
                HashedWheelTimerFuture timerTask = waitingTasks.poll();
                if (timerTask == null) {
                    return;// 结束任务
                }
                // 计算任务的偏移量
                long offset = timerTask.targetTime - startTime;
                // 计算任务需要走的指针步数
                timerTask.totalTicks = offset / tickDuration;
                // 取余计算 bucket index
                int index = (int) (timerTask.totalTicks & mask);
                HashedWheelBucket bucket = wheel[index];

                // TimerTask 维护 Bucket 引用，用于删除该任务
                timerTask.bucket = bucket;
                if (timerTask.status == HashedWheelTimerFuture.WAITING) {
                    bucket.add(timerTask);
                }
            }
        }

        /**
         * 处理被取消的任务
         */
        private void processCanceledTasks() {
            while (true) {
                // 从取消任务队列中取出任务
                HashedWheelTimerFuture canceledTask = canceledTasks.poll();
                if (canceledTask == null) {
                    return;
                }
                // 从链表中删除该任务（bucket为null说明还没被正式推入时间格中，不需要处理）
                if (canceledTask != null) {
                    canceledTask.bucket.remove(canceledTask);
                }
            }
        }

    }
}
