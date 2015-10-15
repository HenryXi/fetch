package com.henry.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.Objects;

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

    public void error(Exception e,String... messages) {
        logger.error(combineMessages(messages,e));
        logger.error(getStackTrace());
    }

    private String combineMessages(Object[] messages){
        return combineMessages(messages,null);
    }
    
    private String combineMessages(Object[] messages,Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        if(e!=null){
            stringBuilder.append(e.getMessage()+"\n\t");
        }
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
