package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

/**
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 10:31
 */
class Notifier implements Runnable {
    private Monitor monitor;

    public Notifier(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (monitor){
                if (monitor.isExecuteNotifyAll()) {
                    monitor.startAt();
                    monitor.notifyAll();
                    break;
                }
            }
        }
        LogHelper.debug(this.getClass(), "Notifier finished its mission!!!!!!");
    }
}
