package com.unwire.journeyservice.constant;

public enum ResponseCode {
    SUCCESS( "0", "Success" ),
    ERROR_INTERNAL_SERVER( "500", "Internal Server Error" ),
    NOT_FOUND( "404", "Not found {0}" ),
    INVALID_USER_PERMISSION( "001", "User cannot retrieve journey {0}" ),
    BACKEND_ERROR("002","Backend Error")
    ;

    private final String code;
    private final String message;


    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private ResponseCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
