package org.goormuniv.ponnect.exception.category;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class RemoveCardOfCategoryException extends CustomException {


    public RemoveCardOfCategoryException (){
        super(ErrCode.REMOVE_CARD_OF_CATEGORY_FAILED, HttpStatus.NOT_ACCEPTABLE);
    }
}