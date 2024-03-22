package org.goormuniv.ponnect.exception.auth;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class STMPException extends CustomException {

    public STMPException (){
        super(ErrCode.REISSUE_FAILED, HttpStatus.NOT_ACCEPTABLE);
    }
}