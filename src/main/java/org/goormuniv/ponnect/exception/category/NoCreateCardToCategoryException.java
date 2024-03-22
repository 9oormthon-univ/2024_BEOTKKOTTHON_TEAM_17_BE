package org.goormuniv.ponnect.exception.category;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NoCreateCardToCategoryException extends CustomException {


    public NoCreateCardToCategoryException (){
        super(ErrCode.CREATE_CARD_TO_CATEGORY_FAILED, HttpStatus.NOT_ACCEPTABLE);
    }
}