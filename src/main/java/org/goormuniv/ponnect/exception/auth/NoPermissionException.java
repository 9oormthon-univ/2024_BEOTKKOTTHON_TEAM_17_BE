package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NoPermissionException extends CustomException {
    public NoPermissionException(){
        super(ErrCode.NO_PERMISSION, HttpStatus.NOT_ACCEPTABLE);
    }
}