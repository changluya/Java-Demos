[toc]

# 前言

本章节配套源码：

+ gitee：https://gitee.com/changluJava/demo-exer/tree/master/JUC/src/main/java/demo12

# 认识定时线程池

## 什么是定时线程池？

定时线程池是一种专门用于执行定时任务的线程池，它结合了线程池的优势和定时任务的功能，能够高效地管理和调度任务。

定时线程池是一种特殊的线程池，它不仅可以执行普通任务，还可以安排任务在未来某个时间点执行，或者以固定的速率重复执行。

在Java中，`ScheduledThreadPoolExecutor`是实现定时线程池的核心类。

---

## 定时线程池基本API使用

1、创建定时线程池

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(5); // 创建一个包含5个线程的定时任务线程池
```

2、提交一次性任务

```java
executor.schedule(new Task("one-time"), 1, TimeUnit.SECONDS); // 在1秒后执行任务
```

3、提交固定速率任务

```java
executor.scheduleAtFixedRate(new Task("fixed-rate"), 2, 3, TimeUnit.SECONDS); // 在2秒后开始执行任务，每隔3秒重复执行
```

4、提交固定延迟任务

```java
executor.scheduleWithFixedDelay(new Task("fixed-delay"), 2, 3, TimeUnit.SECONDS); // 在2秒后开始执行任务，每次执行完毕后等待3秒再执行下一次
```

5、关闭线程池

```java
executor.shutdown(); // 优雅关闭线程池，等待所有任务完成
executor.shutdownNow(); // 强制关闭线程池，停止所有任务
```

## 定时线程池的应用场景

### 1、定时任务调度

**场景描述**：需要定期执行某些任务，比如每天凌晨清理日志、每小时统计系统数据等。

**实现方式**：使用 `scheduleAtFixedRate` 或 `scheduleWithFixedDelay` 方法，设置任务的执行间隔。

**示例**：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
executor.scheduleAtFixedRate(() -> {
    System.out.println("执行每日数据备份任务: " + new Date());
}, 0, 24, TimeUnit.HOURS); // 每隔24小时执行一次
```

------

### 2、缓存过期清理

**场景描述**：在缓存系统中，需要定期清理过期的缓存数据。

**实现方式**：使用定时线程池定期扫描缓存，清理过期的数据。

**示例**：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
executor.scheduleAtFixedRate(() -> {
    cache.cleanExpiredEntries(); // 清理过期缓存
    System.out.println("缓存清理完成: " + new Date());
}, 0, 1, TimeUnit.HOURS); // 每隔1小时清理一次
```

------

### 3、心跳检测

**场景描述**：在分布式系统中，需要定期向其他服务发送心跳包，检测服务是否存活。

**实现方式**：使用定时线程池定期发送心跳请求。

**示例**：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
executor.scheduleAtFixedRate(() -> {
    boolean isAlive = heartBeatCheck(); // 发送心跳检测
    if (!isAlive) {
        System.out.println("服务不可用，触发告警！");
    }
}, 0, 10, TimeUnit.SECONDS); // 每隔10秒检测一次
```

------

### 4、延迟任务执行

**场景描述**：某些任务需要延迟一段时间后执行，比如订单超时未支付自动取消。

**实现方式**：使用 `schedule` 方法，设置任务的延迟时间。

**示例**：

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
executor.schedule(() -> {
    orderService.cancelOrder(orderId); // 取消订单
    System.out.println("订单已取消: " + orderId);
}, 30, TimeUnit.MINUTES); // 延迟30分钟执行
```

---

# 定时线程池scheduleAtFixedRate与scheduleWithFixedDelay区别

![image-20250205224359033](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502052244291.png)

## scheduleAtFixedRate案例demo（period，任务之间间隔）

说明：scheduleAtFixedRate 方法：第三个参数是period，表示每间隔period时间执行一次任务（如果period为2s，但是前一个任务为3s，此时会在前一个任务执行完后再执行）。

```java
public class ScheduledThreadPoolExecutorDemo {
    public static void main(String[] args) {
				test01();
    }

    // scheduleAtFixedRate 方法：第三个参数是period，表示每间隔period时间执行一次任务（如果period为2s，但是前一个任务为3s，此时会在前一个任务执行完后再执行）
    public static void test01() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        // 第0s开始执行，每间隔2s执行一次任务
        executor.scheduleAtFixedRate(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            System.out.println("Task started at: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ", thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(3000); // 模拟任务耗时3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalDateTime endTime = LocalDateTime.now();
            System.out.println("Task finished at: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }, 0, 2, TimeUnit.SECONDS);
    }

}
```

效果如下：

![image-20250205224710125](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502052247778.png)

## scheduleWithFixedDelay案例demo（delay，上一个任务结束后间隔时间）

说明：scheduleWithFixedDelay：第三个参数为delay，指的是**上一个任务执行完之后间隔delay来执行任务**。

```java
public class ScheduledThreadPoolExecutorDemo {
    public static void main(String[] args) {
        test02();
    }
    // scheduleWithFixedDelay：第三个参数为delay，指的是上一个任务执行完之后间隔delay来执行任务
    public static void test02() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            System.out.println("Task started at: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ", thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(3000); // 模拟任务耗时3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalDateTime endTime = LocalDateTime.now();
            System.out.println("Task finished at: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }, 0, 2, TimeUnit.SECONDS);
    }
}

```

效果如下：

![image-20250205225001345](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502052250813.png)

# ScheduledThreadPoolExecutor源码

## 如何实现队列任务的捞取（即任务的调度和执行）？

### 认识DelayedWorkQueue

`DelayedWorkQueue`是一个基于优先队列的无界阻塞队列，用于存储`ScheduledFutureTask`对象。它按照任务的触发时间排序，确保最早到期的任务优先出队。

关键方法

- **`poll`**：从队列中取出最早到期的任务。
- **`offer`**：将任务添加到队列中。
- **`take`**：阻塞等待，直到队列中有任务到期。

**任务的封装（ScheduledFutureTask）**：`ScheduledFutureTask`是`ScheduledThreadPoolExecutor`中用于封装任务的类。它继承自`FutureTask`，并添加了与调度相关的逻辑。

关键字段

- **`sequenceNumber`**：任务的序列号，用于解决触发时间相同的情况。
- **`period`**：任务的周期（对于`scheduleAtFixedRate`）或延迟（对于`scheduleWithFixedDelay`）。
- **`time`**：任务的下次触发时间。

关键方法

- **`run`**：执行任务逻辑。
- **`setNextRunTime`**：设置任务的下次触发时间。

### 理解DelayedWorkQueue#take

**核心任务调度：**`ScheduledThreadPoolExecutor`的工作线程会不断地从`DelayedWorkQueue`中捞取任务并执行。

`DelayedWorkQueue`的`take`方法

```java
public ScheduledFutureTask<?> take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        for (;;) {
            // 获取当前时间
            long currentTime = now();
            // 获取队列头部的任务
            ScheduledFutureTask<?> first = queue.peek();
            if (first == null) {
                // 如果队列为空，等待
                available.await();
            } else {
                // 如果任务已经到期
                if (first.isPeriodic() && first.getPeriod() < 0) {
                    // 周期性任务，重新计算下次触发时间
                    long period = first.getPeriod();
                    long nextTime = first.getNextExecutionTime();
                    first.setNextExecutionTime(nextTime + period);
                    first.setPeriod(period);
                }
                if (first.getDelay(NANOSECONDS) <= 0) {
                    // 任务到期，移除并返回
                    return queue.poll();
                }
                // 如果任务未到期，等待
                long delay = first.getDelay(NANOSECONDS);
                if (leader == null) {
                    leader = Thread.currentThread();
                    available.awaitNanos(delay);
                    leader = null;
                } else {
                    available.awaitNanos(delay);
                }
            }
        }
    } finally {
        lock.unlock();
    }
}
```

### 捞取和执行流程

**捞取和执行流程如下：**

1. **任务添加到队列**：
    - 当调用`scheduleAtFixedRate`或`scheduleWithFixedDelay`时，任务被封装为`ScheduledFutureTask`对象，并添加到`DelayedWorkQueue`中。
    - `DelayedWorkQueue`会按照任务的触发时间排序。
2. **工作线程捞取任务**：
    - 工作线程通过调用`DelayedWorkQueue`的`take`方法来获取最早到期的任务。
    - 如果任务尚未到期，工作线程会阻塞等待。
3. **任务执行**：
    - 工作线程获取任务后，调用任务的`run`方法来执行任务。
    - 如果任务是周期性任务（如`scheduleAtFixedRate`），任务执行完成后会重新计算下次触发时间，并再次添加到队列中。







---

# 参考文章

[1]. Java并发包线程池之ScheduledThreadPoolExecutor：https://www.cnblogs.com/txmfz/p/11222873.html



---

整理者：长路 时间：2025.2.5

