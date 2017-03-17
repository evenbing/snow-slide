package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

/**
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 10:31
 */
class SnowTask implements Runnable {
    private int taskExecuteNumPerThread;
    private Monitor monitor;
    private SnowTaskRunner runners;
    private long taskExecuteMaxMilliLimit;

    public SnowTask(int taskExecuteNumPerThread, Monitor monitor, SnowTaskRunner runners, long taskExecuteMaxMilliLimit) {
        this.taskExecuteNumPerThread = taskExecuteNumPerThread;
        this.monitor = monitor;
        this.runners = runners;
        this.taskExecuteMaxMilliLimit = taskExecuteMaxMilliLimit;
    }

    @Override
    public void run() {
        synchronized (monitor) {
            monitor.incCurrentThreadCount();
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogHelper.debug(this.getClass(), String.format("Staring runSnowTask task at [threadName:%s]", Thread.currentThread()
                .getName()));
        for (int i = 0; i < taskExecuteNumPerThread; i++) {
            StopWatch watch = new StopWatch(taskExecuteMaxMilliLimit,monitor);
            watch.start();
            runners.runSnowTask();
            watch.stop();
        }
    }
}
