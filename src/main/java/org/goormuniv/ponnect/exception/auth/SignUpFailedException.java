package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class SignUpFailedException extends CustomException {
    public SignUpFailedException(){
        super(ErrCode.SIGN_UP_FAILED, HttpStatus.NOT_ACCEPTABLE);
    }
}