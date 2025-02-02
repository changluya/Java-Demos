[toc]

# 线程池各类场景描述

**类型场景：**不同的场景设置参数也各不相同

+ **第一种**：CPU密集型：最大线程数应该等于CPU核数+1，这样最大限度提高效率。

```java
// 通过该代码获取当前运行环境的cpu核数
Runtime.getRuntime().availableProcessors();
```

+ **第二种：**IO密集型：主要是进行IO操作，执行IO操作的时间较长，这时cpu出于空闲状态，导致cpu的利用率不高。线程数为2倍CPU核数。当其中的线程在IO操作的时候，其他线程可以继续用cpu，提高了cpu的利用率。

+ **第三种：混合型**：如果CPU密集型和IO密集型执行时间相差不大那么可以拆分；如果两种执行时间相差很大，就没必要拆分了。

+ **第四种(了解)：**在IO优化中，线程等待时间所占比越高，需要线程数越多；线程cpu时间占比越高，需要越少线程数。

**线程池初始化所有参数：**

```shell
corePoolSize : 核心线程数，当线程池中的线程数量为 corePoolSize 时，即使这些线程处于空闲状态，也不会销毁（除非设置 allowCoreThreadTimeOut）。
maximumPoolSize : 最大线程数，线程池中允许的线程数量的最大值。
keepAliveTime : 线程空闲时间，当线程池中的线程数大于 corePoolSize 时，多余的空闲线程将在销毁之前等待新任务的最长时间。
workQueue : 任务队列
unit ： 线程空闲时间的单位。
threadFactory ： 线程工厂，线程池创建线程时使用的工厂。
handler : 拒绝策略，因达到线程边界和任务队列满时，针对新任务的处理方法。
	CallerRunsPolicy：由提交任务的线程直接执行任务，避免任务丢失。适合任务量波动较大的场景。
	AbortPolicy：直接抛出 RejectedExecutionException 异常。适合任务量可控的场景。
	DiscardPolicy：静默丢弃任务，不抛出异常。适合对任务丢失不敏感的场景。
	DiscardOldestPolicy：丢弃队列中最旧的任务，然后重新尝试提交当前任务。适合对任务时效性要求较高的场景。
```

**核心线程池execute逻辑代码：**

```java
public void execute(Runnable command) {
		//任务判空
        if (command == null)
            throw new NullPointerException();
       	//查看当前运行的线程数量
        int c = ctl.get();
    	//若小于核心线程则直接添加一个工作线程并执行任务
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        //如果线程数等于核心线程数则尝试将任务入队
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        //入队失败，调用addWorker参数为false，尝试创建应急线程处理突发任务
        else if (!addWorker(command, false))
        	//如果创建应急线程失败，说明当前线程数已经大于最大线程数，这个任务只能拒绝了
            reject(command);
    }

```



![image-20250201004416120](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502010044371.png)



---

# 常见场景案例设计思路

## 公共类

![image-20250202175504266](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021755850.png)

### 自定义工厂类-MyThreadFactory

`MyThreadFactory.java`：自定义了线程池工厂类，可以自行进行命名

```java
package demo10;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程池工厂类
 */
public class MyThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public MyThreadFactory(String factoryName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = factoryName + "-pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
```

### 自定义拒绝策略-RejectedExecutionHandlerFactory

`RejectedExecutionHandlerFactory.java`：包含有多种拒绝策略，其中包含本次需要使用的阻塞入队拒绝策略

```java
package demo10;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 拒绝策略工厂类
 *
 */
@Slf4j
public class RejectedExecutionHandlerFactory {

    private static final AtomicLong COUNTER = new AtomicLong();

    /**
     * 拒绝执行，抛出 RejectedExecutionException
     * @param source name for log
     * @return A handler for tasks that cannot be executed by ThreadPool
     */
    public static RejectedExecutionHandler newAbort(String source) {
        return (r, e) -> {
            log.error("[{}] ThreadPool[{}] overload, the task[{}] will be Abort, Maybe you need to adjust the ThreadPool config!", source, e, r);
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " + source);
        };
    }

    /**
     * 直接丢弃该任务
     * @param source log name
     * @return A handler for tasks that cannot be executed by ThreadPool
     */
    public static RejectedExecutionHandler newDiscard(String source) {
        return (r, p) -> {
            log.error("[{}] ThreadPool[{}] overload, the task[{}] will be Discard, Maybe you need to adjust the ThreadPool config!", source, p, r);
        };
    }

    /**
     * 调用线程运行
     * @param source log name
     * @return A handler for tasks that cannot be executed by ThreadPool
     */
    public static RejectedExecutionHandler newCallerRun(String source) {
        System.out.println("thread =>" + Thread.currentThread().getName() + "触发阻塞中...");
        return (r, p) -> {
            log.error("[{}] ThreadPool[{}] overload, the task[{}] will run by caller thread, Maybe you need to adjust the ThreadPool config!", source, p, r);
            if (!p.isShutdown()) {
                r.run();
            }
        };
    }

    /**
     * 新线程运行
     * @param source log name
     * @return A handler for tasks that cannot be executed by ThreadPool
     */
    public static RejectedExecutionHandler newThreadRun(String source) {
        return (r, p) -> {
            log.error("[{}] ThreadPool[{}] overload, the task[{}] will run by a new thread!, Maybe you need to adjust the ThreadPool config!", source, p, r);
            if (!p.isShutdown()) {
                String threadName = source + "-T-" + COUNTER.getAndIncrement();
                log.info("[{}] create new thread[{}] to run job", source, threadName);
                new Thread(r, threadName).start();
            }
        };
    }

    /**
     * 依据阻塞队列put 阻塞添加到队列中
     * @return 拒绝策略执行器
     */
    public static RejectedExecutionHandler blockCallerPolicy(String source) {
        return new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                log.error("[{}] ThreadPool[{}] overload, the task[{}] will run by caller thread, Maybe you need to adjust the ThreadPool config!", source, e, r);
                if (!e.isShutdown()) {
                    try {
                        // 阻塞入队操作，阻塞方为调用方执行submitjob的线程
                        e.getQueue().put(r);
                    } catch (InterruptedException ex) {
                        log.error("reject put queue error", ex);
                    }
                }
            }
        };
    }


}
```

### 自定义阻塞队列-TaskQueue（实现 核心线程->最大线程数->队列）

`TaskQueue.java`：线程池中实现先使用核心线程数

```java
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
```



## 场景1：CPU密集型场景

### 思路&计算公式

**场景：**具体是指那种包含大量运算、在持有的 CPU 分配的时间片上一直在执行任务、几乎不需要依赖或等待其他任何东西。处理起来其实没有多少优化空间，因为处理时几乎没有等待时间，所以一直占有 CPU 进行执行，才是最好的方式。

**可优化的点：**就是当单个线程累计较多任务时，其他线程能进行分担，类似`fork/join框架`的概念。

**设置参数：**设置线程数时，针对**单台机器，最好就是有几个 CPU ，就创建几个线程**，然后每个线程都在执行这种任务，永不停歇。

```shell
Nthreads=Ncpu+1 
w/c =0 
理解也是正确的，+1 主要是防止因为系统上下文切换，让系统资源跑满！
```

### 实现代码

![image-20250202183217126](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021832612.png)

`IOIntensiveThreadPoolExample2.java`：这里最终实现的Example2类来进行测试

```java
package demo10.io;

import demo10.MyThreadFactory;
import demo10.RejectedExecutionHandlerFactory;
import demo10.TaskQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * io密集型场景任务提交
 * demo3：基于demo2自定义拒绝策略
 *  自定义队列：核心线程 -> 最大线程 -> 队列
 *  自定义拒绝策略：自定义采用执行阻塞队列的put操作来实现任务阻塞入队，而非直接使用调用者线程来直接跑任务
 *  非影响主线程执行流程：批次1000个任务统一在一个线程中去进行处理，与主流程main线程隔离
 *
 */
@Slf4j
public class IOIntensiveThreadPoolExample2 {

    public static void main(String[] args) {
        // 获取 CPU 核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 自定义线程池参数
        int corePoolSize = cpuCores * 2; // 核心线程数（IO 密集型任务可以设置较大）
        int maximumPoolSize = cpuCores * 4; // 最大线程数
        long keepAliveTime = 60L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        // 自定义任务队列 核心线程 -> 最大核心线程数 -> 队列
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(corePoolSize * 2); // 队列容量为核心线程数的 2 倍
        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                taskQueue,
                new MyThreadFactory("IOIntensiveThreadPool"), // 默认线程工厂 Executors.defaultThreadFactory() | 自定义工厂支持自定义线程池名字
                RejectedExecutionHandlerFactory.blockCallerPolicy("IOIntensiveThreadPool")
        );
        // 将线程池对象设置到任务队列中
        taskQueue.setExecutor(executor);

        // 统计任务的执行数量
        int jobNums = 1000;
        final AtomicInteger count = new AtomicInteger(0);

        // 记录任务开始时间
        long startTime = System.currentTimeMillis();
        // 单独开一个线程（后续可改为线程池 核心、最大就1个场景）去完成整个任务提交处理
        // 如果submitjob阻塞，仅仅只会影响该thread线程
        new Thread(() -> {
            CountDownLatch latch = new CountDownLatch(jobNums);
            // 模拟1000个任务 （可改造为queue队列形式去在这个线程中去消费）
            for (int i = 0; i < jobNums; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println(Thread.currentThread().getName() + " 正在执行任务 " + taskId + "...");
                    try {
                        Thread.sleep(500); // 模拟 IO 操作（如网络请求或文件读写）10s
                        // xxxio类耗时操作
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }finally {
                        System.out.println(Thread.currentThread().getName() + " 任务 " + taskId + " 完成！");
                        count.incrementAndGet(); // 原子操作，+1 并返回新值
                        latch.countDown();
                    }
                });
            }
            System.out.println("所有任务提交完成！");
            // 关闭线程池，等待任务全部执行完毕
            try {
                latch.await();
                System.out.println("所有任务执行结束！");
                // 记录任务结束时间
                long endTime = System.currentTimeMillis();
                // 计算任务执行时间
                long duration = endTime - startTime;
                System.out.println("任务执行总耗时: " + duration + " 毫秒");
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }finally {
                executor.shutdown();
            }
        }).start();

        try {
            // 等待所有任务完成
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // 强制关闭
            }
            System.out.println("执行完任务数统计：" + count.get());
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

一个任务耗时0.5s，1000个任务执行如下：

![image-20250202183457306](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021834776.png)

说明：经过测试验证，如果IO阻塞时间特别长，调大最大核心线程数效果更好。

---

## 场景2：IO密集型场景

### 思路&计算公式

**场景：**其消耗的主要资源就是 IO 了，所接触到的 IO ，大致可以分成两种：`磁盘 IO`和`网络 IO`。IO 操作的特点就是需要等待，我们请求一些数据，由对方将数据写入`缓冲区`，在这段时间中，需要读取数据的线程根本无事可做，因此可以把 CPU 时间片让出去，直到`缓冲区`写满。

+ 磁盘 IO ，大多都是一些针对磁盘的读写操作，最常见的就是文件的读写，假如你的数据库、 Redis 也是在本地的话，那么这个也属于磁盘 IO。
+ 网络 IO ，这个应该是大家更加熟悉的，我们会遇到各种网络请求，比如 http 请求、远程数据库读写、远程 Redis 读写等等。

**设置参数：**

```shell
# 如果存在IO，那么肯定w/c>1（阻塞耗时一般都是计算耗时的很多倍）,但是需要考虑系统内存有限（每开启一个线程都需要内存空间），这里需要上服务器测试具体多少个线程数适合（CPU占比、线程数、总耗时、内存消耗）。如果不想去测试，保守点取1即，Nthreads=Ncpu*(1+1)=2Ncpu。这样设置一般都OK
# 通用就是2倍的CPU核心数（如果要效率最大化，需要测算当前系统环境每个线程任务的阻塞等待时间与实际计算时间）
Nthreads=Ncpu*(1+w/c)
公式中 W/C 为系统 阻塞率  w:等待时间 c:计算时间
```

---

### 实现代码

![image-20250202184351200](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021843736.png)

这里核心+最大线程数使用的是CPU核心数+1：

```java
package demo10.cpu;

import demo10.MyThreadFactory;
import demo10.RejectedExecutionHandlerFactory;
import demo10.TaskQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * cpu密集型场景任务提交
 *  自定义队列：核心线程 -> 最大线程 -> 队列
 *  自定义拒绝策略：自定义采用执行阻塞队列的put操作来实现任务阻塞入队，而非直接使用调用者线程来直接跑任务
 *  非影响主线程执行流程：批次1000个任务统一在一个线程中去进行处理，与主流程main线程隔离
 *
 */
@Slf4j
public class CPUThreadPoolExample {

    public static void main(String[] args) {
        // 获取 CPU 核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();

        // 自定义线程池参数
        int corePoolSize = cpuCores + 1; // 核心线程数 cpu核心数+1
        int maximumPoolSize = corePoolSize; // 最大线程数 cpu核心数+1
        long keepAliveTime = 60L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        // 自定义任务队列 核心线程 -> 最大核心线程数 -> 队列
        TaskQueue<Runnable> taskQueue = new TaskQueue<>(500); // 队列容量为核心线程数的 2 倍
        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                taskQueue,
                new MyThreadFactory("IOIntensiveThreadPool"), // 默认线程工厂 Executors.defaultThreadFactory() | 自定义工厂支持自定义线程池名字
                RejectedExecutionHandlerFactory.blockCallerPolicy("IOIntensiveThreadPool")
        );
        // 将线程池对象设置到任务队列中
        taskQueue.setExecutor(executor);

        // 统计任务的执行数量
        int jobNums = 1000000;
        final AtomicInteger count = new AtomicInteger(0);

        // 记录任务开始时间
        long startTime = System.currentTimeMillis();
        // 单独开一个线程（后续可改为线程池 核心、最大就1个场景）去完成整个任务提交处理
        // 如果submitjob阻塞，仅仅只会影响该thread线程
        new Thread(() -> {
            CountDownLatch latch = new CountDownLatch(jobNums);
            // 模拟1000个任务 （可改造为queue队列形式去在这个线程中去消费）
            for (int i = 0; i < jobNums; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    // CPU计算
                    int sum = 0;
                    for (int j = 0; j < 100000; j++) {
                        sum += j;
                    }
                    System.out.println(Thread.currentThread().getName() + " 任务 " + taskId + " 完成！sum = " + sum);
                    count.incrementAndGet(); // 原子操作，+1 并返回新值
                    latch.countDown();
                });
            }
            System.out.println("所有任务提交完成！");
            // 关闭线程池，等待任务全部执行完毕
            try {
                latch.await();
                System.out.println("所有任务执行结束！");
                // 记录任务结束时间
                long endTime = System.currentTimeMillis();
                // 计算任务执行时间
                long duration = endTime - startTime;
                System.out.println("任务执行总耗时: " + duration + " 毫秒");
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }finally {
                executor.shutdown();
            }
        }).start();

        try {
            // 等待所有任务完成
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // 强制关闭
            }
            System.out.println("执行完任务数统计：" + count.get());
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

效果：

![image-20250202184444336](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502021844567.png)





---

# 其他部分组成

## 拒绝策略兜底方案

### 思路设计及思考

如果核心线程、最大线程、队列都满了的情况下该如何处理？如果本身就是单台机器资源打满，就需要在设计策略上改变线程池的调度方案，**如果我的目的是任何一个任务都不丢弃，同时在服务器上有余力及时处理？**

**方案1：持久化数据库设计**

+ 如：设计一张任务表间任务存储到 MySQL 数据库中；redis缓存；任务提交到中间件来缓冲。

设计思路可以如下：参考https://zhuanlan.zhihu.com/p/700719289

![image-20250201084054264](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202502010840537.png)

**方案2：**Netty 为例，它的拒绝策略则是**直接创建一个线程池以外的线程处理这些任务**，为了保证任务的实时处理，这种做法可能需要良好的硬件设备且临时创建的线程无法做到准确的监控。

+ 后续通过翻阅源码发现一种在拒绝策略场景**带退避的重试策略**。

**方案3：**ActiveMQ 则是尝试**在指定的时效内尽可能的争取将任务入队**，以保证最大交付

**方案4：**dubbo设计思路（dump文件+抛出异常）

**方案5：线程阻塞队列**

思路：队列采用阻塞队列，在拒绝策略方法中使用put方法实现阻塞效果。

可能情况：阻塞主线程任务执行。

---

### 设计1：数据库持久化方案

设计思路：自定义拒绝策略，在拒绝策略情况下进行数据库持久化；自定义实现队列，在poll的时候优先从db获取任务，接着再从队列中获取。

**详细具体实现可见：**某大厂线程池拒绝策略连环问 https://blog.csdn.net/shark_chili3007/article/details/137042400

---

### 设计2：Netty两种拒绝策略实现（根据场景来进行是否重试入队 + 失败抛异常）

**实现思路1：创建新线程执行任务**

说明：为了保证任务的实时处理，这种做法**需要良好的硬件设备且临时创建的线程无法做到准确的监控**。

```java
private static final class NewThreadRunsPolicy implements RejectedExecutionHandler {
    NewThreadRunsPolicy() {
        super();
    }
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            //创建一个临时线程处理任务
            final Thread t = new Thread(r, "Temporary task executor");
            t.start();
        } catch (Throwable e) {
            throw new RejectedExecutionException(
                    "Failed to start a new thread", e);
        }
    }
}
```

弊端：如果任务数特别多无上限场景，就会出现oom情况，导致服务挂掉。

**实现思路2：**拒绝策略场景**带退避的重试策略**。

源码地址：https://github.dev/netty/netty

+ 具体代码文件：RejectedExecutionHandlers

```java
/**
 * Tries to backoff when the task can not be added due restrictions for an configured amount of time. This
 * is only done if the task was added from outside of the event loop which means
 * {@link EventExecutor#inEventLoop()} returns {@code false}.
 */
public static RejectedExecutionHandler backoff(final int retries, long backoffAmount, TimeUnit unit) {
    // 检查 retries 参数是否为正数，如果不是则抛出异常
    ObjectUtil.checkPositive(retries, "retries");

    // 将退避时间转换为纳秒
    final long backOffNanos = unit.toNanos(backoffAmount);

    // 返回一个实现了 RejectedExecutionHandler 接口的匿名类
    return new RejectedExecutionHandler() {
        @Override
        public void rejected(Runnable task, SingleThreadEventExecutor executor) {
            // 检查当前线程是否不是事件循环线程
            if (!executor.inEventLoop()) {
                // 进行最多 retries 次重试
                for (int i = 0; i < retries; i++) {
                    // 尝试唤醒事件循环线程，以便它能够处理任务队列中的任务
                    executor.wakeup(false);

                    // 当前线程休眠指定的退避时间
                    LockSupport.parkNanos(backOffNanos);

                    // 尝试将任务重新加入任务队列
                    if (executor.offerTask(task)) {
                        // 如果任务成功加入队列，则直接返回
                        return;
                    }
                }
            }
            // 如果当前线程是事件循环线程，或者重试次数用尽后仍然无法加入任务队列，
            // 则抛出 RejectedExecutionException 异常
            throw new RejectedExecutionException();
        }
    };
}
```

---

### 设计3：ActiveMQ（有效时间内尝试入队+入队失败抛出异常）

说明：尝试**在指定的时效内尽可能的争取将任务入队**，以保证最大交付，超过时间内则返回false。

github地址：https://github.dev/apache/activemq

+ 对应代码：BrokerService#getExecutor

```java
new RejectedExecutionHandler() {
      @Override
      public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
          try {
            	// 在60s内进行尝试入队，如果入队失败，则抛出异常
              if (!executor.getQueue().offer(r, 60, TimeUnit.SECONDS)) {
                  throw new RejectedExecutionException("Timed Out while attempting to enqueue Task.");
              }
          } catch (InterruptedException e) {
              throw new RejectedExecutionException("Interrupted waiting for BrokerService.worker");
          }
      }
  }
```

### 设计4：dubbo设计思路（dump文件+抛出异常）

github地址：

```java
@Override
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    String msg = String.format(
            "Thread pool is EXHAUSTED!"
                    + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d),"
                    + " Task: %d (completed: %d),"
                    + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in %s://%s:%d!",
            threadName,
            e.getPoolSize(),
            e.getActiveCount(),
            e.getCorePoolSize(),
            e.getMaximumPoolSize(),
            e.getLargestPoolSize(),
            e.getTaskCount(),
            e.getCompletedTaskCount(),
            e.isShutdown(),
            e.isTerminated(),
            e.isTerminating(),
            url.getProtocol(),
            url.getIp(),
            url.getPort());

    // 0-1 - Thread pool is EXHAUSTED!
    logger.warn(COMMON_THREAD_POOL_EXHAUSTED, "too much client requesting provider", "", msg);

    if (Boolean.parseBoolean(url.getParameter(DUMP_ENABLE, Boolean.TRUE.toString()))) {
        // 进行dump文件
        dumpJStack();
    }
		// 指派发送消息给listener监听器
    dispatchThreadPoolExhaustedEvent(msg);

    throw new RejectedExecutionException(msg);
}
```

dubbo的工作线程触发了线程拒绝后，主要做了三个事情，原则就是尽量**让使用者清楚触发线程拒绝策略的真实原因**。

1）输出了一条警告级别的日志，日志内容为线程池的详细设置参数，以及线程池当前的状态，还有当前拒绝任务的一些详细信息。可以说，这条日志，使用dubbo的有过生产运维经验的或多或少是见过的，这个日志简直就是日志打印的典范，其他的日志打印的典范还有spring。得益于这么详细的日志，可以很容易定位到问题所在

2）输出当前线程堆栈详情，这个太有用了，当你通过上面的日志信息还不能定位问题时，案发现场的dump线程上下文信息就是你发现问题的救命稻草。

3）继续抛出拒绝执行异常，使本次任务失败，这个继承了JDK默认拒绝策略的特性

---

### 设计5： 自定义设计-阻塞入队

**在线程池初始化的时候自定义拒绝策略**：阻塞入队操作，阻塞方为调用方执行submitjob的线程

```java
new RejectedExecutionHandler() {
  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
      log.error("[{}] ThreadPool[{}] overload, the task[{}] will run by caller thread, Maybe you need to adjust the ThreadPool config!", "IOIntensiveThreadPool", e, r);
      if (!e.isShutdown()) {
          try {
              // 阻塞入队操作，阻塞方为调用方执行submitjob的线程
              e.getQueue().put(r);
          } catch (InterruptedException ex) {
              log.error("reject put queue error", ex);
          }
      }
  }
}
```

如果要执行的任务数量过多，核心线程数、最大核心线程数占满、任务队列占满，此时让任务进行入队阻塞，等待队列中任务有空余位置。











---

# 参考

[1]. Java 线程池讲解——针对 IO 密集型任务：https://www.jianshu.com/p/66b6dfcf3173（提出dubbo 或者 tomcat 的线程池中自定义Queue的实现，核心线程数 -> 最大线程数 -> 队列中）

[2]. 某大厂线程池拒绝策略连环问 https://blog.csdn.net/shark_chili3007/article/details/137042400

[3]. 线程池拒绝策略：https://blog.csdn.net/qq_40428665/article/details/121680262

[4]. Java线程池如何合理配置核心线程数：https://www.cnblogs.com/Vincent-yuan/p/16022613.html

[5]. 线程池参数配置：https://blog.csdn.net/whp404/article/details/131960756（计算公式）