package org.goormuniv.ponnect.exception.category;

import org.goormuniv.ponnect.exception.CustomException;
import org.goormuniv.ponnect.exception.ErrCode;
import org.springframework.http.HttpStatus;

public class RenameCategoryException extends CustomException {


    public RenameCategoryException (){
        super(ErrCode.RENAME_CATEGORY_FAILED, HttpStatus.NOT_ACCEPTABLE);
    }
}