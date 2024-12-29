package com.changlu.redission.component;

import com.changlu.redission.tx.CustomizedTransactionTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class CLRedissionLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(CLRedissionLock.class);

    private final RedissonClient redissonClient;

    private final CustomizedTransactionTemplate customizedTransactionTemplate;

    public CLRedissionLock(RedissonClient redissonClient,
                          CustomizedTransactionTemplate customizedTransactionTemplate) {
        this.redissonClient = redissonClient;
        this.customizedTransactionTemplate = customizedTransactionTemplate;
    }

    // ==================== 无返回值 =============================

    /**
     * 执行任务 by 非公平锁
     * 开启watch dog，默认超时时间为30s，每隔10s进行续期(线程)
     */
    public void execWithLockWithWatchDog(final String lockKey, long lockWaitTime, TimeUnit unit,
                                             Runnable runnable, Consumer<Throwable> exceptionConsumer, Runnable getLockFailRunnable, boolean needTx) {
        // 开启watch：lockLeaseTime = -1
        execWithLock(lockKey, lockWaitTime, -1, unit, runnable, exceptionConsumer, getLockFailRunnable, needTx);
    }

    /**
     * 无入参，无返回值场景使用
     * @param lockKey 锁名
     * @param lockWaitTime try lock等待时间
     * @param lockLeaseTime lock过期时间
     * @param unit 时间单位
     * @param runnable 任务
     * @param exceptionConsumer 异常处理器
     * @param needTx 是否需要事务
     */
    public void execWithLock(final String lockKey, long lockWaitTime, long lockLeaseTime, TimeUnit unit,
                             Runnable runnable, Consumer<Throwable> exceptionConsumer, Runnable getLockFailRunnable, boolean needTx) {
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean lock = rLock.tryLock(
                    lockWaitTime,
                    lockLeaseTime,
                    unit);
            if (lock) {
                LOGGER.info("CLRedissionLock get lock: {}", lockKey);
                if (needTx) {
                    customizedTransactionTemplate.execute(runnable);
                } else {
                    runnable.run();
                }
            }else {
                LOGGER.info("CLRedissionLock get lock: {} fail", lockKey);
                getLockFailRunnable.run();
            }
        } catch (Throwable e) {
            exceptionConsumer.accept(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                LOGGER.info("CLRedissionLock release lock: {}", lockKey);
            }
        }
    }

    // ==================== 带返回值 =============================
    /**
     * 执行任务 by 非公平锁
     * 开启watch dog，默认超时时间为30s，每隔10s进行续期
     */
    public <T, R> R execWithLockWithWatchDog(final String lockKey, long lockWaitTime, TimeUnit unit,
                                 T param,
                                 Function<T, R> function,
                                 Consumer<Throwable> exceptionConsumer,
                                 Supplier<R> defaultSupplier,
                                 boolean needTx) {
        // 开启watch：lockLeaseTime = -1
        return execWithLock(lockKey, lockWaitTime, -1, unit, param, function, exceptionConsumer, defaultSupplier, needTx);
    }

    /**
     * 执行任务 by 非公平锁
     */
    public <T, R> R execWithLock(final String lockKey, long lockWaitTime, long lockLeaseTime, TimeUnit unit,
                                 T param,
                                 Function<T, R> function,
                                 Consumer<Throwable> exceptionConsumer,
                                 Supplier<R> defaultSupplier,
                                 boolean needTx) {
        return execWithLock(lockKey, lockWaitTime, lockLeaseTime, unit, param, function, exceptionConsumer, defaultSupplier, needTx, false);
    }

    /**
     *
     * @param lockKey 锁名
     * @param lockWaitTime try lock等待时间
     * @param lockLeaseTime lock过期时间
     * @param unit 时间单位
     * @param param 要执行的无返回值操作
     * @param function 要执行的有返回值操作
     * @param defaultSupplier 异常处理器
     * @param exceptionConsumer 获取锁失败时执行的操作
     * @param needTx 是否需要事务
     * @param isFair 是否使用公平锁
     * @return 返回结果
     * @param <T> 有返回值操作的参数类型
     * @param <R> 有返回值操作的返回值类型
     */
    public <T, R> R execWithLock(final String lockKey, long lockWaitTime, long lockLeaseTime, TimeUnit unit,
                                 T param,
                                 Function<T, R> function,
                                 Consumer<Throwable> exceptionConsumer,
                                 Supplier<R> defaultSupplier,
                                 boolean needTx,
                                 boolean isFair) {
        RLock rLock = isFair ? redissonClient.getFairLock(lockKey) : redissonClient.getLock(lockKey);
        try {
            boolean lock = rLock.tryLock(
                    lockWaitTime,
                    lockLeaseTime,
                    unit);
            if (lock) {
                LOGGER.info("CLRedissionLock function get lock: {}", lockKey);
                if (needTx) {
                    return customizedTransactionTemplate.executeWithThrow(function, param);
                } else {
                    return function.apply(param);
                }
            }
        } catch (Throwable e) {
            exceptionConsumer.accept(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                LOGGER.info("CLRedissionLock function release lock: {}", lockKey);
            }
        }
        LOGGER.info("CLRedissionLock function get lock: {} fail", lockKey);
        return defaultSupplier.get();
    }
}
