package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class AlreadyMemberException extends CustomException {
    public AlreadyMemberException (){
        super(ErrCode.ALREADY_MEMBER, HttpStatus.BAD_REQUEST);
    }
}