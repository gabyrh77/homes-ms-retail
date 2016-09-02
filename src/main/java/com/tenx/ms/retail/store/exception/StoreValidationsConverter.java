package com.tenx.ms.retail.store.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goropeza on 01/09/16.
 */
@Component
public class StoreValidationsConverter {

    public List<ObjectError> getDataIntegrityErrors(DataIntegrityViolationException ex) {List<ObjectError> result = new ArrayList<>();
        if (ex.getMessage().contains("UNIQUE_STORE_NAME")) {
            result.add(new ObjectError("name", "is unique and already exists"));
        }
        return result;
    }
}
