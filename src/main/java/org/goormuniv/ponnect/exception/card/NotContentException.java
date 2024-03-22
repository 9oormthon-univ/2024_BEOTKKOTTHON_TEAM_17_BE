package org.goormuniv.ponnect.exception.card;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NotContentException extends CustomException {
    public NotContentException() {
        super(ErrCode.NOT_CONTENT, HttpStatus.NOT_ACCEPTABLE);
    }
}
