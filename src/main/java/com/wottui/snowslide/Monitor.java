package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

import java.math.BigDecimal;
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

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd hh:mm:ss");

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
        System.out.println(alreadyExecuteTaskNum);
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
        long executeAllMilliTime = endAt - startAt;
        BigDecimal bigDecimalExecuteAllMilliTime = new BigDecimal(executeAllMilliTime);
        BigDecimal executeCount = new BigDecimal(threadMax * taskExecuteNumPerThread);
        double averagePerExecuteMilli = bigDecimalExecuteAllMilliTime.divide(executeCount).doubleValue();
        double passRate = new BigDecimal(successNum).divide(new BigDecimal(startAtDisplay + failureNum)).doubleValue();
        System.out.print("TaskName:" + testName + ",");
        System.out.print("startAt:" + startAtDisplay + ",");
        System.out.print("endAt:" + endAtDisplay + ",");
        System.out.print("times:" + testName + "ms,");
        System.out.print("avgPerTimes:" + averagePerExecuteMilli + ",");
        System.out.print("qualified-num:" + successNum + ",");
        System.out.print("unqualified-num" + failureNum + ",");
        System.out.print("pass rate" + passRate * 100 + "%");
    }
}
