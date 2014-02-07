package com.dilax.mobile.model.network.util;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

public class LoggerWrapper {

    private final Logger log;

    public LoggerWrapper(final Logger log) {
        this.log = log;
    }

    public void debug(final String message, final Object... objects) {
        log.info(message, objects);
    }

    public void error(final String message, final Object... objects) {
        log.error(message, objects);

    }

    public void error(final Throwable throwable, final String message, final Object... objects) {
        log.error(MessageFormatter.format(message, objects).getMessage(), throwable);

    }

    public void info(final String message, final Object... objects) {
        log.info(message, objects);
    }

    public void trace(final String message, final Object... objects) {
        log.info(message, objects);
    }

    public void warn(final String message, final Object... objects) {
        log.warn(message, objects);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

}