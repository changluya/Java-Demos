[toc]

# 认识SynchronousQueue

`SynchronousQueue` 是一个特殊的队列，它的核心特点是 **不存储元素**，每个插入操作必须等待一个对应的移除操作，反之亦然。这种特性使得它非常适合一些特定的应用场景。

这种机制非常适合处理大量短期异步任务的场景，例如 Web 服务器处理请求、短时计算任务等。

`SynchronousQueue` 和 `LinkedBlockingQueue` 是 Java 并发包中两种不同的队列实现，它们的设计目标和使用场景有显著区别。以下是它们的主要区别。

**关于SynchronousQueue的公平与非公平体现在多线程场景情况**：

+ 公平：多个线程进行put阻塞的话，按照谁先进行put会优先去执行任务。（底层是队列）
+ 非公平：多个线程进行put阻塞的话，并非是排队的，而是会以栈的结构先进后出。（底层是栈）

关于公平非公平源码解析：[Java阻塞队列中的异类，SynchronousQueue底层实现原理剖析](https://zhuanlan.zhihu.com/p/584514846)



> BlockingQueue接口说明

**BlockingQueue设计了很多的放数据和取数据的方法**。

| 操作               | 抛出异常  | 返回特定值 | 阻塞   | 阻塞一段时间         |
| ------------------ | --------- | ---------- | ------ | -------------------- |
| 放数据             | add       | offer      | put    | offer(e, time, unit) |
| 取数据             | remove    | poll       | take   | poll(time, unit)     |
| 查看数据（不删除） | element() | peek()     | 不支持 | 不支持               |

这几组方法的不同之处就是：

1. 当队列满了，再往队列中放数据，add方法抛异常，offer方法返回false，put方法会一直阻塞（直到有其他线程从队列中取走数据），offer(e, time, unit)方法阻塞指定时间然后返回false。
2. 当队列是空，再从队列中取数据，remove方法抛异常，poll方法返回null，take方法会一直阻塞（直到有其他线程往队列中放数据），poll(time, unit)方法阻塞指定时间然后返回null。
3. 当队列是空，再去队列中查看数据（并不删除数据），element方法抛异常，peek方法返回null。

工作中使用最多的就是offer、poll阻塞指定时间的方法。





---

# 基本对比及比较

## 1. **基本特性**

| **特性**     | **SynchronousQueue**                       | **LinkedBlockingQueue**                            |
| :----------- | :----------------------------------------- | :------------------------------------------------- |
| **容量**     | 容量为 0，不存储元素。                     | 容量可配置（默认 `Integer.MAX_VALUE`），存储元素。 |
| **阻塞行为** | 插入操作必须等待对应的移除操作，反之亦然。 | 队列满时插入操作阻塞，队列空时移除操作阻塞。       |
| **适用场景** | 直接传递任务，适合线程池任务调度。         | 缓冲任务，适合生产者-消费者模式。                  |



## 2. **内部实现**

| **实现**     | **SynchronousQueue**                       | **LinkedBlockingQueue**                                   |
| :----------- | :----------------------------------------- | :-------------------------------------------------------- |
| **数据结构** | 无存储结构，直接传递元素。                 | 基于链表实现，存储元素。                                  |
| **锁机制**   | 使用 CAS 或锁实现线程间的直接匹配。        | 使用两把锁（`putLock` 和 `takeLock`）分离插入和移除操作。 |
| **公平性**   | 支持公平模式（FIFO）和非公平模式（默认）。 | 默认非公平，但可以通过锁实现公平性。                      |



## 3. **性能特点**

| **性能**     | **SynchronousQueue**               | **LinkedBlockingQueue**            |
| :----------- | :--------------------------------- | :--------------------------------- |
| **吞吐量**   | 高吞吐量，适合直接传递任务的场景。 | 吞吐量较低，因为需要维护队列结构。 |
| **延迟**     | 低延迟，任务直接传递给消费者。     | 延迟较高，任务需要先入队再出队。   |
| **内存占用** | 内存占用低，不存储元素。           | 内存占用高，需要存储队列中的元素。 |



## 4. **使用场景**

| **场景**          | **SynchronousQueue**                                       | **LinkedBlockingQueue**               |
| :---------------- | :--------------------------------------------------------- | :------------------------------------ |
| **任务调度**      | 适合线程池任务调度（如 `Executors.newCachedThreadPool`）。 | 适合需要缓冲任务的场景。              |
| **生产者-消费者** | 适合生产者直接传递任务给消费者。                           | 适合生产者-消费者模式，任务需要缓冲。 |
| **流量控制**      | 无缓冲能力，严格限制生产者和消费者的同步。                 | 提供缓冲能力，适合流量控制。          |



## 5. **总结对比**

| **对比项**   | **SynchronousQueue**               | **LinkedBlockingQueue**              |
| :----------- | :--------------------------------- | :----------------------------------- |
| **容量**     | 无容量，直接传递任务。             | 有容量，可缓冲任务。                 |
| **阻塞行为** | 插入和移除操作必须成对出现。       | 队列满时插入阻塞，队列空时移除阻塞。 |
| **性能**     | 高吞吐量，低延迟。                 | 吞吐量较低，延迟较高。               |
| **适用场景** | 直接传递任务，适合线程池任务调度。 | 缓冲任务，适合生产者-消费者模式。    |

选择使用哪种队列取决于具体的应用场景：

- 如果需要直接传递任务且不需要缓冲，选择 `SynchronousQueue`。
- 如果需要缓冲任务并控制流量，选择 `LinkedBlockingQueue`。

---

# SynchronousQueue案例

**如果你希望你的任务需要被快速处理**，就可以使用这种队列。

## JDK应用案例

Java线程池中的**newCachedThreadPool**（带缓存的线程池）底层就是使用SynchronousQueue实现的。

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
}
```

弊端：可能会出现oom问题。如果你提交了太多的任务，导致创建了大量的线程，这些线程都在竞争CPU时间片，等待CPU调度，处理任务速度也会变慢，所以在使用过程中也要综合考虑。

---

## 案例1：SynchronousQueue的简单用例

![image-20250202153336290](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021533267.png)

```java
package demo10.cpu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @apiNote SynchronousQueue示例
 **/
public class SynchronousQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. 创建SynchronousQueue队列
        BlockingQueue<Integer> synchronousQueue = new SynchronousQueue<>();

        // 2. 启动一个线程，往队列中放3个元素
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 入队列 1");
                synchronousQueue.put(1);
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 入队列 2");
                synchronousQueue.put(2);
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 入队列 3");
                synchronousQueue.put(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // 3. 等待1000毫秒
        Thread.sleep(1000L);

        // 4. 再启动一个线程，从队列中取出3个元素
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 出队列 " + synchronousQueue.take());
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 出队列 " + synchronousQueue.take());
                Thread.sleep(1);
                System.out.println(Thread.currentThread().getName() + " 出队列 " + synchronousQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
```

![image-20250202141622478](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021416086.png)

效果如下：第一个线程Thread-0往队列放入一个元素1后，就被阻塞了。直到第二个线程Thread-1从队列中取走元素1后，Thread-0才能继续放入第二个元素2。

---

## 案例2：SynchronousQueue公平锁、非公平锁案例

![image-20250202153350415](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021533521.png)

**为什么要多线程场景去测试？**

+ 单线程你没法直接连续put多个，因为put和take操作是相对的，put了一个之后，只有take了另一个才能再进行put操作。

```java
package demo11;

import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;

/**
 * 公平 & 非公平 案例测试
 * 设置初始化的SynchronousQueue参数即可
 */
public class SynchronousQueueFairAndNotFairDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. 创建SynchronousQueue队列，可设置是否公平
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>(false);

        // 放置三个元素 放置过程中各自等待500ms
        for (int i = 0; i < 10; i++) {
            // 2. 启动一个线程，往队列中放1个元素
            int finalI = i;
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "开始入队列" + finalI);
                    synchronousQueue.put(finalI);
//                    System.out.println(Thread.currentThread().getName() + " 入队列" + finalI + "成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            Thread.sleep(500);
        }
        // 3. 等待1000毫秒
//        Thread.sleep(1000L);

      	// 取元素的时候各自间隔500ms
        int[] arr = new int[10];
        for (int i = 0; i < 10; i++) {
            // 4. 启动一个线程，往队列中放1个元素
            int finalI = i;
            new Thread(() -> {
                try {
                    arr[finalI] = synchronousQueue.take();
                    System.out.println(Thread.currentThread().getName() + " 出队列 " + arr[finalI]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            Thread.sleep(500);
        }
        System.out.println(Arrays.toString(arr));
        // 7. 等待1000毫秒
        Thread.sleep(1000L);
    }
}
```

**测试1：当设置非公平情况**

```java
SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>(false);
```

![image-20250202153656442](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021536712.png)

**测试2：当设置公平情况**

```java
SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>(true);
```

![image-20250202153735555](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021537852.png)

---

## 案例3：搭配线程池使用

### 线程池多线程+SynchronousQueue（公平无效）

![image-20250202173128343](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021731581.png)

配合线程池多线程场景，SynchronousQueue设置为公平无效，测试源码如下：

```java
package demo11;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池多线程场景（核心数、最大线程数非1场景）
 * 无公平可言原因：线程池底层走的是offer操作，并非是put操作（可见案例2中的demo场景，可实现公平是走的put操作实现）
 * 适合场景：cpu密集型，在多线程场景通过线程池是无法实现按照submit的提交顺序去处理逻辑的（原因如上）。
 */
public class SynchronousQueueFairAndNotFairPoolDemo1 {
    public static void main(String[] args) throws InterruptedException {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(cpuCores + 1, cpuCores + 1,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(true),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            // 阻塞等待
                            executor.getQueue().put(r);
                        } catch (InterruptedException var4) {
                            throw new RejectedExecutionException("Unexpected InterruptedException", var4);
                        }
                    }
                });
        // 倒计数
        int jobNum = 100;
        CountDownLatch countDownLatch = new CountDownLatch(jobNum);
        final AtomicInteger count = new AtomicInteger(0);
        // 记录任务开始时间
        long startTime = System.currentTimeMillis();
        //设计提交任务
        new Thread(()->{
            // 可改为queue队列
            int i = 0;
            while (i < jobNum) {
                int finalI = i;
                // submitjob的线程（非主线程）会进入到阻塞当中（保证按照顺序来执行）
                // 线程池底层使用的是队列的offer、poll
                executor.submit(()->{
                    System.out.println("CPU执行任务：" + finalI + ", 计数 =>" + count.incrementAndGet());
                    countDownLatch.countDown();
                });

                i++;
            }
        }).start();

        System.out.println("main主线程开始干活");
        countDownLatch.await();
        executor.shutdown();
        System.out.println("任务全部完成");

        // 记录任务结束时间
        long endTime = System.currentTimeMillis();
        // 计算任务执行时间
        long duration = endTime - startTime;
        System.out.println("任务执行总耗时: " + duration + " 毫秒");
    }
}

```

![image-20250202173220376](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021732583.png)

---

### 线程池多线程+SynchronousQueue（公平有效）

![image-20250202173234582](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021732743.png)

```java
package demo11;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池单线程场景（核心数、最大线程数1场景）
 * 可实现公平效果（无论是否设置公平参数）
 * 适合场景：任务按照submitjob去依次提交任务 & 去除线程池同样也可实现，可见下面处理方式
 */
public class SynchronousQueueFairAndNotFairPoolDemo2 {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), // 填写false、true在核心、最大线程数为1 1情况下效果一致
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            executor.getQueue().put(r);
                        } catch (InterruptedException var4) {
                            throw new RejectedExecutionException("Unexpected InterruptedException", var4);
                        }
                    }
                });

        // 倒计数
        int jobNum = 100;
        CountDownLatch countDownLatch = new CountDownLatch(jobNum);
        final AtomicInteger count = new AtomicInteger(0);
        //设计提交任务
        new Thread(()->{
            // 可改为queue队列
            int i = 0;
            while (i < jobNum) {
                int finalI = i;
                // 处理方式一：submitjob的线程（非主线程）会进入到阻塞当中（保证按照顺序来执行）
                executor.submit(()->{
                    System.out.println("CPU执行任务：" + finalI + ", 计数 =>" + count.incrementAndGet());
                    countDownLatch.countDown();
                });
                // or 处理方式二：思考：是否如果原本就在新建线程中，是否无需使用线程池去submitjob提交？因为原本当前就是阻塞进行的
//                System.out.println("执行任务：" + finalI);

                i++;
            }
        }).start();

        // 7. 等待1000毫秒
        System.out.println("main主线程开始干活");
        countDownLatch.await();
        executor.shutdown();
        System.out.println("任务全部完成");
    }
}
```

效果：

![image-20250202174350317](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021743628.png)















---

整理者：长路  时间：2025.2.2