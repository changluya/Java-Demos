package com.changlu.powerjob;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定时器
 */
public interface Timer {

    /**
     * 调度定时任务
     */
    TimeFuture schedule(TimerTask task, long delay, TimeUnit unit);

    /**
     * 停止所有调度任务
     */
    Set<TimerTask> stop();

}
