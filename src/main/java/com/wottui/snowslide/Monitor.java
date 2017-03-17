package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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

    public Monitor(int threadMax, int taskExecuteNumPerThread, String testName) {
        this.currentThreadCount = 0;
        this.threadMax = threadMax;
        this.alreadyExecuteTaskNum = 0;
        this.taskExecuteNumPerThread = taskExecuteNumPerThread;
        this.successNum = 0;
        this.failureNum = 0;
        this.testName = testName;

    }

    public void incCurrentThreadCount() {
        currentThreadCount++;
    }

    public void incAlreadyExecuteTaskNum() {
        lockTaskInc.lock();
        alreadyExecuteTaskNum++;
        lockTaskInc.unlock();
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
        if (threadMax * taskExecuteNumPerThread == alreadyExecuteTaskNum) {
            endAt = System.currentTimeMillis();
            LogHelper.debug(this.getClass(), "All task is end...");
            return true;
        }
        return false;
    }

    public void calculate() {
        String endAtDisplay = FORMAT.format(endAt);
        String startAtDisplay = FORMAT.format(startAt);
        BigDecimal executeAllMilliTime = new BigDecimal(endAt - startAt);
        BigDecimal executeCount = new BigDecimal(successNum + failureNum);
        BigDecimal averagePerExecuteMilli = executeAllMilliTime.divide(executeCount,10,BigDecimal.ROUND_HALF_DOWN);
        BigDecimal passRate = new BigDecimal(successNum).divide(new BigDecimal(successNum + failureNum),2,BigDecimal.ROUND_HALF_DOWN);
        BigDecimal allSecond = executeAllMilliTime.divide(new BigDecimal(1000)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal QPS = executeCount.divide(allSecond,0,BigDecimal.ROUND_HALF_DOWN);

        System.out.println("TaskName: " + testName + "");
        System.out.println("StartAt: " + startAtDisplay + "");
        System.out.println("EndAt: " + endAtDisplay + "");
        System.out.println("Times: " + executeAllMilliTime + "ms");
        System.out.println("AllRequest-num: " + executeCount);
        System.out.println("AvgPerTimes: " + averagePerExecuteMilli + "ms");
        System.out.println("Qualified-num: " + successNum + "");
        System.out.println("Unqualified-num: " + failureNum + "");
        System.out.println("Pass rate:" + passRate.doubleValue() * 100 + "%");
        System.out.println("QPS: " + QPS.doubleValue());
    }
}
