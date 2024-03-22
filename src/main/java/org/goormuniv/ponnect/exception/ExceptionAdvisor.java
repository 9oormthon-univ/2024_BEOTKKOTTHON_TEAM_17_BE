package org.goormuniv.ponnect.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvisor {


    @ExceptionHandler(CustomException.class)
        protected ResponseEntity<ErrResponse> handleCustomException(CustomException exception) {
            log.info("handleCustomException ::{}", exception.getMessage());
            return ResponseEntity
                    .status(exception.getStatus())
                    .body(new ErrResponse(exception.getErrorCode()));
        }

}
