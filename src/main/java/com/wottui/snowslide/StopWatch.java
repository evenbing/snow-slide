package com.wottui.snowslide;

/**
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 11:26
 */
public class StopWatch {
    private long startAt;
    private long stopAt;
    private long taskExecuteMaxMilliLimit;
    private Monitor monitor;

    public StopWatch(long taskExecuteMaxMilliLimit, Monitor monitor) {
        this.taskExecuteMaxMilliLimit = taskExecuteMaxMilliLimit;
        this.monitor = monitor;
    }

    public void start() {
        startAt = System.currentTimeMillis();
    }

    public void stop() {
        monitor.incAlreadyExecuteTaskNum();
        stopAt = System.currentTimeMillis();
        if (stopAt - startAt > taskExecuteMaxMilliLimit) {
            markFailure();
        } else {
            markSuccess();
        }
    }

    private void markSuccess() {
        monitor.incSuccess();
    }

    private void markFailure() {
        monitor.incFailure();
    }
}
