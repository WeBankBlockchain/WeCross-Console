package com.webank.wecross.console.exception;

public class WeCrossConsoleException extends java.lang.Exception {

    private static final long serialVersionUID = 3754251447587995515L;

    private Integer errorCode;

    public WeCrossConsoleException(Integer code, String message) {
        super(message);
        errorCode = code;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
