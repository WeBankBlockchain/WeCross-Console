package com.webank.wecross.console.exception;

public class ErrorCode {

    // common
    public static final int INTERNAL_ERROR = 1;

    // init wecross service
    public static final int INIT_WECROSS_SERVICE_ERROR = 1001;
    public static final int HAVE_NOT_LOGGED_IN = 1002;

    // status in utils
    public static final int ILLEGAL_PARAM = 2001;
    public static final int METHOD_MISSING = 2002;
    public static final int PARAM_MISSING = 2003;
    public static final int INVALID_PATH = 2004;
    public static final int NO_RESPONSE = 2005;

    // status in routine
    public static final int INVALID_TXID = 3001;
}
