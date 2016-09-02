package com.tenx.ms.retail.product.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goropeza on 01/09/16.
 */
@Component
public class ProductValidationsConverter {

    public List<ObjectError> getDataIntegrityErrors(DataIntegrityViolationException ex) {List<ObjectError> result = new ArrayList<>();
        if (ex.getMessage().contains("UNIQUE_PRODUCT_SKU_STORE")) {
            result.add(new ObjectError("sku", "is unique and already exists"));
        }
        return result;
    }
}
