package com.supplychain.exception;

import com.supplychain.common.ResultCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final Integer code;
    private final String message;

    public UnauthorizedException(String message) {
        this(ResultCode.UNAUTHORIZED.getCode(), message);
    }

    public UnauthorizedException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public UnauthorizedException(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }
}
