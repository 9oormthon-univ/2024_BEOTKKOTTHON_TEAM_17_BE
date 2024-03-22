package org.goormuniv.ponnect.exception.category;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NoExistCardOfCategoryException extends CustomException {


    public NoExistCardOfCategoryException (){
        super(ErrCode.NO_EXIST_CARD_OF_CATEGORY, HttpStatus.NOT_FOUND);
    }
}