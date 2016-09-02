package com.tenx.ms.retail.client.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goropeza on 01/09/16.
 */
@Component
public class ClientValidationsConverter {

    public List<ObjectError> getDataIntegrityErrors(DataIntegrityViolationException ex) {List<ObjectError> result = new ArrayList<>();
        if (ex.getMessage().contains("UNIQUE_CLIENT_EMAIL")) {
            result.add(new ObjectError("email", "is unique and already exists"));
        }
        return result;
    }
}
