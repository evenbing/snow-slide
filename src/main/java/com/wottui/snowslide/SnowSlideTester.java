package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 雪崩并发测试
 *
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 10:17
 */
public class SnowSlideTester {
    private int threadCount;            //并发数
    private int taskExecuteNumPerThread;//每个并发线程任务执行数
    private int taskRunMaxMilliLimit;   //每个任务允许执行最大的毫秒数
    private ExecutorService taskExecutor = Executors.newCachedThreadPool();
    private Monitor monitor;
    private String testName;

    public SnowSlideTester(int threadCount, int taskExecuteNumPerThread, int taskRunMaxMilliLimit, String testName) {
        this.threadCount = threadCount;
        this.taskExecuteNumPerThread = taskExecuteNumPerThread;
        this.taskRunMaxMilliLimit = taskRunMaxMilliLimit;
        monitor = new Monitor(threadCount, taskExecuteNumPerThread, testName);
        this.testName = testName;
    }

    private SnowSlideTester(SnowSlideTestBuilder builder) {
        this.threadCount = builder.threadCount;
        this.taskExecuteNumPerThread = builder.taskExecuteNumPerThread;
        this.taskRunMaxMilliLimit = builder.taskRunMaxMilliLimit;
        this.testName = builder.testName;
        monitor = new Monitor(threadCount, taskExecuteNumPerThread, testName);
    }

    public void startTest(SnowTaskRunner runner) {
        LogHelper.info(this.getClass(), "Console is running...");
        new Console(monitor).console();
        startTask(runner);
        LogHelper.info(this.getClass(), "All task staring");
        startNotifier();
        LogHelper.info(this.getClass(), "Notifier staring");
    }

    private void startTask(SnowTaskRunner runner) {
        for (int i = 0; i < threadCount; i++)
            taskExecutor.execute(new SnowTask(taskExecuteNumPerThread, monitor, runner, taskRunMaxMilliLimit));
    }

    private void startNotifier() {
        taskExecutor.execute(new Notifier(monitor));
    }

    public static class SnowSlideTestBuilder {
        private int threadCount;
        private int taskExecuteNumPerThread;
        private int taskRunMaxMilliLimit;
        private String testName;

        public SnowSlideTestBuilder threadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public SnowSlideTestBuilder taskExecuteNumPerThread(int taskExecuteNumPerThread) {
            this.taskExecuteNumPerThread = taskExecuteNumPerThread;
            return this;
        }

        public SnowSlideTestBuilder taskRunMaxMilliLimit(int taskRunMaxMilliLimit) {
            this.taskRunMaxMilliLimit = taskRunMaxMilliLimit;
            return this;
        }

        public SnowSlideTestBuilder testName(String testName) {
            this.testName = testName;
            return this;
        }

        public SnowSlideTester build() {
            return new SnowSlideTester(this);
        }
    }

}
