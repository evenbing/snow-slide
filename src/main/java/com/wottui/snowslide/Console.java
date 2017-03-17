package com.wottui.snowslide;


import com.wottui.utils.LogHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 13:51
 */
public class Console {
    private Monitor monitor;
    private ExecutorService taskExecutor = Executors.newCachedThreadPool();

    public Console(Monitor monitor) {
        this.monitor = monitor;
    }

    public void console() {
        taskExecutor.execute(new ConsoleTask(monitor));
    }

    public static class ConsoleTask implements Runnable {
        private Monitor monitor;

        public ConsoleTask(Monitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (true) {
                if (monitor.isStop()) {
                    monitor.calculate();
                    break;
                }
            }
            LogHelper.debug(this.getClass(), "Console exit!!!!");
        }
    }
}
