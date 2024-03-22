package org.goormuniv.ponnect.exception.follow;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NoExistFollowMemberException extends CustomException {
    public NoExistFollowMemberException(){
        super(ErrCode.NO_EXIST_FOLLOW_MEMBER, HttpStatus.BAD_REQUEST);
    }
}

