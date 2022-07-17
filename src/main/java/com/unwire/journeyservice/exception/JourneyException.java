package com.unwire.journeyservice.exception;

import java.text.MessageFormat;

public class JourneyException extends RuntimeException {

    final String code;
    final transient Object[] args;

    public JourneyException(String code, String message, Object... args) {
        super( message );
        this.code = code;
        this.args = args;
    }

    public JourneyException(String code, String message, boolean isBackend) {
        super( message );
        this.code = code;
        this.args = null;
    }

    public JourneyException(String code, String message, Throwable e, Object... args) {
        super( message, e );
        this.code = code;
        this.args = args;
    }

    public String getMessage() {
        return MessageFormat.format( super.getMessage(), this.args );
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.args;
    }
}

