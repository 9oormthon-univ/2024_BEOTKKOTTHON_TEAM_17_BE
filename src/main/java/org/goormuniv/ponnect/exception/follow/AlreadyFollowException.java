package org.goormuniv.ponnect.exception.follow;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class AlreadyFollowException extends CustomException {


    public AlreadyFollowException(){
        super(ErrCode.ALREADY_FOLLOW, HttpStatus.BAD_REQUEST);
    }
}
