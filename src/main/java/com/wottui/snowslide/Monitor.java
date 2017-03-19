package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 10:32
 */
class Monitor {
    private int currentThreadCount;
    private int threadMax;
    private int alreadyExecuteTaskNum; //已经执行的任务数
    private Lock lockTaskInc = new ReentrantLock();
    private Lock lockSuccessInc = new ReentrantLock();
    private Lock lockFailureInc = new ReentrantLock();
    private int taskExecuteNumPerThread;
    private long startAt;
    private long endAt;
    private int successNum;
    private int failureNum;
    private String testName;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private BlockingQueue<Long> queue;
    private long executeAllMilliTime;
    private ExecutorService executorService = Executors.newCachedThreadPool();


    public Monitor(int threadMax, int taskExecuteNumPerThread, String testName) {
        this.currentThreadCount = 0;
        this.threadMax = threadMax;
        this.alreadyExecuteTaskNum = 0;
        this.taskExecuteNumPerThread = taskExecuteNumPerThread;
        this.successNum = 0;
        this.failureNum = 0;
        this.testName = testName;
        queue = new LinkedBlockingQueue<>();
        executorService.execute(new Consumer(this));
    }

    public void incCurrentThreadCount() {
        currentThreadCount++;
    }

    public void incAlreadyExecuteTaskNum() {
        lockTaskInc.lock();
        alreadyExecuteTaskNum++;
        lockTaskInc.unlock();
    }

    void saveExecuteTime(Long time) {
        try {
            queue.put(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void incSuccess() {
        lockSuccessInc.lock();
        successNum++;
        lockSuccessInc.unlock();
    }

    public void incFailure() {
        lockFailureInc.lock();
        failureNum++;
        lockFailureInc.unlock();
    }

    public void startAt() {
        LogHelper.debug(this.getClass(), "Test is running...");
        startAt = System.currentTimeMillis();
    }

    public boolean isExecuteNotifyAll() {
        return currentThreadCount >= threadMax;
    }

    public boolean isStop() {
        if (threadMax * taskExecuteNumPerThread == alreadyExecuteTaskNum&&queue.isEmpty()) {
            endAt = System.currentTimeMillis();
            LogHelper.debug(this.getClass(), "All task is end...");
            return true;
        }
        return false;
    }

    public void calculate() {

        System.out.println(executeAllMilliTime);
        String endAtDisplay = FORMAT.format(endAt);
        String startAtDisplay = FORMAT.format(startAt);
        BigDecimal executeCount = new BigDecimal(successNum + failureNum);
        BigDecimal executeAllMilliTimeBig = new BigDecimal(executeAllMilliTime);
        BigDecimal averagePerExecuteMilli = executeCount.divide(executeAllMilliTimeBig, 2, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal passRate = new BigDecimal(successNum).divide(new BigDecimal(successNum + failureNum), 2, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal allSecond = executeAllMilliTimeBig.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal QPS = executeCount.divide(allSecond, 0, BigDecimal.ROUND_HALF_DOWN);

        System.out.println("TaskName: " + testName + "");
        System.out.println("StartAt: " + startAtDisplay + "");
        System.out.println("EndAt: " + endAtDisplay + "");
        System.out.println("Times: " + (endAt - startAt) + "ms");
        System.out.println("AllRequest-num: " + executeCount);
        System.out.println("PerRequest-AvgPerTimes: " + averagePerExecuteMilli + "s");
        System.out.println("Request-Qualified-num: " + successNum + "");
        System.out.println("Request-Unqualified-num: " + failureNum + "");
        System.out.println("Request-Pass rate:" + passRate.doubleValue() * 100 + "%");
        System.out.println("QPS: " + QPS.doubleValue());
    }

    private static class Consumer implements Runnable {
        private Monitor monitor;

        public Consumer(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (true) {
                while (!monitor.queue.isEmpty()) {
                    try {
                        monitor.executeAllMilliTime = monitor.executeAllMilliTime + monitor.queue.take().longValue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogHelper.debug(this.getClass(), "Consumer executeAllMilliTime " + monitor.executeAllMilliTime);
                }
            }
        }
    }

}
