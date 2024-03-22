package org.goormuniv.ponnect.exception.server;

import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

@Slf4j
public class InternalServerException extends CustomException {
    public InternalServerException(Exception exception) {
        super(ErrCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        log.info("Internal Server Error ::{}", exception.getMessage());
    }
}