package org.goormuniv.ponnect.exception.card;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NoExistCardException extends CustomException {
    public NoExistCardException() {
        super(ErrCode.NO_EXIST_CARD, HttpStatus.NOT_FOUND);
    }
}
