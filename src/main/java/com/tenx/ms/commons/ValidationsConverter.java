package com.tenx.ms.commons;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by goropeza on 01/09/16.
 */
@Component
public class ValidationsConverter {

    public List<ObjectError> getConstraintValidationErrors(ConstraintViolationException ex) {
        if (ex.getConstraintViolations() != null) {
            return ex.getConstraintViolations().stream()
                .map(constraintViolation -> new ObjectError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()))
                .collect(Collectors.toList());
        }
        return null;
    }

    public List<ObjectError> getTransactionErrors(TransactionSystemException ex) {
        List<ObjectError> result = new ArrayList<>();
        if (ex.getMostSpecificCause() instanceof ConstraintViolationException) {
            return getConstraintValidationErrors((ConstraintViolationException)ex.getMostSpecificCause());
        }
        return result;
    }
}
