package com.re.spring.boot.springbootdemo.thread;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程池
 */
@Component
public class AsyncThreadPool {
    private static final int CORE_POOL_SIZE = 1024;
    private static final int MAX_MUM_POOL_SIZE = 2048;
    private static final int KEEP_ALIVE_TIME = 60000;

    private static final BlockingQueue<Runnable> WORK_QUEUE = new SynchronousQueue<>(true);
    private static final MyThreadFactory MY_THREAD_FACTORY = new MyThreadFactory();
    private static final MyAbortPolicy MY_ABORT_POLICY = new MyAbortPolicy();

    public static final ThreadPoolExecutor ASYNC_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_MUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            WORK_QUEUE, MY_THREAD_FACTORY,
            MY_ABORT_POLICY);

    /**
     * 自定义线程工厂
     */
    static class MyThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "AsyncThreadPool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(@NonNull Runnable r) {
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

    /**
     * 自定义拒绝策略
     */
    static class MyAbortPolicy implements RejectedExecutionHandler {
        /**
         * Creates an {@code AbortPolicy}.
         */
        MyAbortPolicy() {
        }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " +
                    e.toString());
        }
    }
}
