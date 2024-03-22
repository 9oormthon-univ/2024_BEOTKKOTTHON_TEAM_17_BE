package org.goormuniv.ponnect.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {


    private final ErrCode errCode;

    private final HttpStatus httpStatus;


    public CustomException(final ErrCode errCode, final HttpStatus status) {
        super(errCode.getMessage());
        this.errCode = errCode;
        this.httpStatus = status;
    }
//    public CustomException(String message, final ErrCode errCode, final HttpStatus status) {
//        super(message);
//        this.errCode = errCode;
//        this.httpStatus = status;
//    }


    public HttpStatus getStatus() {
        return httpStatus;
    }

    public ErrCode getErrorCode() {
        return errCode;
    }


}
