package org.goormuniv.ponnect.exception.category;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class NoExistCategoryException extends CustomException {


    public NoExistCategoryException (){
        super(ErrCode.NO_EXIST_CATEGORY, HttpStatus.NOT_FOUND);
    }
}