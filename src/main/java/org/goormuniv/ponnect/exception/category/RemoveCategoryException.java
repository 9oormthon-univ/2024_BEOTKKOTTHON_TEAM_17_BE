package org.goormuniv.ponnect.exception.category;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class RemoveCategoryException extends CustomException {


    public RemoveCategoryException (){
        super(ErrCode.REMOVE_CATEGORY_FAILED, HttpStatus.NOT_ACCEPTABLE);
    }
}