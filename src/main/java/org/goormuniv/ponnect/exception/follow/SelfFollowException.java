package org.goormuniv.ponnect.exception.follow;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class SelfFollowException extends CustomException {
    public  SelfFollowException(){
        super(ErrCode.SELF_FOLLOW, HttpStatus.BAD_REQUEST);
    }
}