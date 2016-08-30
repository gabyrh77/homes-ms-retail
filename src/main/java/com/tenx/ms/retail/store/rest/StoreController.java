package com.tenx.ms.retail.store.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.store.rest.dto.Store;
import com.tenx.ms.retail.store.service.StoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by goropeza on 24/08/16.
 */
@Api(value = "store", description = "Store API")
@RestController("storeControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @ApiOperation(value = "Find Store by store id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Store returned successfully"),
        @ApiResponse(code = 404, message = "Store couldn't be found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.GET)
    public Store getStoreById(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId) {
        Store store =  storeService.findStoreById(storeId);
        if (store == null) {
            throw new NoSuchElementException();
        } else {
            return store;
        }
    }

    @ApiOperation(value = "Returns all stores")
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Store list returned successfully"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<Store> getStores(){
        return storeService.findAllStores();
    }

    @ApiOperation(value = "Creates a new store")
    @ApiResponses( value = {
        @ApiResponse(code = 201, message = "Store successfully created"),
        @ApiResponse(code = 400, message = "Invalid parameters"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreated<Long> createStore(@ApiParam(name = "store", value = "Store data", required = true) @RequestBody Store store){
        Store newStore = storeService.insertStore(store);
        return new ResourceCreated<Long>(newStore.getStoreId());
    }

    @ApiOperation(value = "Updates a store given an id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Store successfully updated"),
        @ApiResponse(code = 400, message = "Invalid parameters"),
        @ApiResponse(code = 404, message = "Store can't be found by id"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.PUT)
    public Store updateStore(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                             @ApiParam(name = "store", value = "Store data", required = true) @RequestBody Store store) {
        return storeService.updateStore(storeId, store);
    }

    @ApiOperation(value = "Deletes a store given an id")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Store successfully deleted"),
        @ApiResponse(code = 404, message = "Store can't be found by id"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId) {
        if(!storeService.exists(storeId)) {
            throw  new NoSuchElementException();
        } else {
            storeService.deleteStore(storeId);
        }
    }

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(ConstraintViolationException.class)
    protected void handleConstraintViolationException(ConstraintViolationException ex,
                                                      HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected void handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                                                      HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
    }
}
