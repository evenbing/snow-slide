package com.wotttui.snowslide;

import com.wottui.snowslide.SnowSlideTester;
import com.wottui.snowslide.SnowTaskRunner;

/**
 * @Author: chendonglin
 * @Date: 2017/3/17
 * @Time: 18:34
 */
public class Test {
    public static void main(String[] args) {
        new SnowSlideTester(10000,100,100,"TestApi").startTest(new SnowTaskRunner() {
            @Override
            public void runSnowTask() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
