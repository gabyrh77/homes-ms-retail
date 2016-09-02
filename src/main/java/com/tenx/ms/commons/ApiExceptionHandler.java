package com.tenx.ms.commons;

import com.tenx.ms.commons.rest.SystemError;
import com.tenx.ms.commons.rest.ValidationError;
import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * Created by goropeza on 02/09/16.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @Autowired
    private ValidationsConverter validationsConverter;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException ex) {
        ValidationError error = new ValidationError(ex, null, validationsConverter.getConstraintValidationErrors(ex));
        return new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity handleTransactionSystemException(TransactionSystemException ex)  {
        ValidationError error = new ValidationError(ex, null, validationsConverter.getTransactionErrors(ex));
        return new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(NullArgumentException.class)
    protected ResponseEntity<Object> handleNullArgumentException(NullArgumentException ex) {
        return new ResponseEntity<>(new SystemError(ex.getMessage(), HttpStatus.PRECONDITION_FAILED.value(), ex), HttpStatus.PRECONDITION_FAILED);
    }
}
