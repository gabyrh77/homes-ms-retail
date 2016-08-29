package com.tenx.ms.retail.stock.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.retail.product.service.ProductService;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import com.tenx.ms.retail.stock.service.StockService;
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
import java.util.NoSuchElementException;

/**
 * Created by goropeza on 27/08/16.
 */
@Api(value = "stock", description = "Stock API")
@RestController("stockControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/stock")
public class StockController {
    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @ApiOperation(value = "Add/update a product stock")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Stock successfully updated"),
        @ApiResponse(code = 404, message = "Store or product can't be found by id"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}/{productId:\\d+}"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateStock(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                            @ApiParam(name = "productId", value = "Product id") @PathVariable() Long productId,
                            @ApiParam(name = "stock", value = "Stock data", required = true) @RequestBody Stock stock) {
        if(!productService.existsInStore(productId, storeId)) {
            throw new NoSuchElementException();
        } else {
            stockService.upsertStock(productId, stock);
        }
    }

    @ApiOperation(value = "Return stock by store id and product id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Stock returned successfully"),
        @ApiResponse(code = 404, message = "Store or product couldn't be found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}/{productId:\\d+}"}, method = RequestMethod.GET)
    public Stock getStoreById(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                              @ApiParam(name = "productId", value = "Product id") @PathVariable() Long productId) {
        if(!productService.existsInStore(productId, storeId)) {
            throw new NoSuchElementException();
        } else {
            return stockService.findStockByProduct(productId);
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
