package com.wottui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 *
 * @Author: chendonglin
 * @Date: 2017/1/21
 * @Time: 9:41
 */
public class LogHelper {
    private static boolean open = true;

    private static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void info(Class<?> clazz, String msg) {
        if (!open) return;
        Logger logger = getLogger(clazz);
        logger.info(msg);
    }

    public static void debug(Class<?> clazz, String msg) {
        if (!open) return;
        Logger logger = getLogger(clazz);
        logger.debug(msg);
    }

    public static void warn(Class<?> clazz, String msg) {
        if (!open) return;
        Logger logger = getLogger(clazz);
        logger.warn(msg);
    }

    public static void error(Class<?> clazz, String msg) {
        if (!open) return;
        Logger logger = getLogger(clazz);
        logger.error(msg);
    }
}
