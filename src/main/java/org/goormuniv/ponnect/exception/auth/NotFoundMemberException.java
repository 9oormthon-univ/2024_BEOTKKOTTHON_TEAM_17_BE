package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NotFoundMemberException extends CustomException {
    public NotFoundMemberException(){
        super(ErrCode.NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND);
    }
}