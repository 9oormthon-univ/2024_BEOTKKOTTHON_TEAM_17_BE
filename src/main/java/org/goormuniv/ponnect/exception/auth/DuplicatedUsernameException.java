package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class DuplicatedUsernameException extends CustomException {
    public  DuplicatedUsernameException (){
        super(ErrCode.DUPLICATED_EMAIL, HttpStatus.BAD_REQUEST);
    }
}

