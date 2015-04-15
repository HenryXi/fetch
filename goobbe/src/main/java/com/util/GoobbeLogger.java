package com.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class GoobbeLogger {
    private final Logger logger = Logger.getLogger(this.getClass());

    public void debug(String... messages) {
        logger.debug(combineMessages(messages));
    }

    public void info(String... messages) {
        logger.info(combineMessages(messages));
    }

    public void warn(String... messages) {
        logger.warn(combineMessages(messages));
    }

    public void error(String... messages) {
        logger.error(combineMessages(messages));
        logger.error(getStackTrace());
    }

    private String combineMessages(Object[] messages) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object message : messages) {
            stringBuilder.append(message);
        }
        return stringBuilder.toString();
    }

    private String getStackTrace() {
        StringBuilder stringBuilder = new StringBuilder("Stack Trace INFO:");
        for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
            stringBuilder.append("\n\t").append(stackTrace.toString());
        }
        return stringBuilder.toString();
    }
}
