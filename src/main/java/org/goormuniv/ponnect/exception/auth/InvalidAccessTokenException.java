package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class InvalidAccessTokenException extends CustomException {

    public InvalidAccessTokenException(){
        super(ErrCode.INVALID_ACCESS_TOKEN, HttpStatus.FORBIDDEN);
    }
}