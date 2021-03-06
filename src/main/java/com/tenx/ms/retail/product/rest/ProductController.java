package com.tenx.ms.retail.product.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.ValidationError;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.product.exception.ProductValidationsConverter;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/**
 * Created by goropeza on 26/08/16.
 */
@Api(value = "product", description = "Product API")
@RestController("productControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductValidationsConverter validationsConverter;

    @ApiOperation(value = "Return all products in a store")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product list returned successfully"),
        @ApiResponse(code = 404, message = "Store couldn't be found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.GET)
    public List<Product> findProductsInStore(@ApiParam(name = "storeId", value = "Store id")
                              @PathVariable() Long storeId) {
        return productService.findAllProductsByStore(storeId);
    }

    @ApiOperation(value = "Find a product in a store by product id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product returned successfully"),
        @ApiResponse(code = 404, message = "Store or product couldn't be found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}"}, params={"productId"}, method = RequestMethod.GET)
    public Product findProductInStoreById(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                                          @ApiParam(name = "productId", value = "Product id", required = true) @RequestParam Long productId) {
        return productService.findProductByIdAndStore(productId, storeId);
    }

    @ApiOperation(value = "Find a product in a store by product name")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product returned successfully"),
        @ApiResponse(code = 404, message = "Store or product couldn't be found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}"}, params={"productName"}, method = RequestMethod.GET)
    public Product findProductInStoreByName(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                                            @ApiParam(name = "productName", value = "Product name", required = true) @RequestParam String productName) {
        return productService.findProductByNameAndStore(productName, storeId);
    }

    @ApiOperation(value = "Creates a new product in a store")
    @ApiResponses( value = {
        @ApiResponse(code = 201, message = "Product successfully created"),
        @ApiResponse(code = 400, message = "Invalid parameters"),
        @ApiResponse(code = 404, message = "Store couldn't be found"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreated<Long> createProduct(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                                             @ApiParam(name = "product", value = "Product data", required = true) @RequestBody Product product){
        Product newProduct = productService.createInStore(product, storeId);
        return new ResourceCreated<>(newProduct.getProductId());
    }

    @ApiOperation(value = "Updates a product from a store given an id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product successfully updated"),
        @ApiResponse(code = 400, message = "Invalid parameters"),
        @ApiResponse(code = 404, message = "Store couldn't be found"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}/{productId:\\d+}"}, method = RequestMethod.PUT)
    public Product updateProduct(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                            @ApiParam(name = "productId", value = "Product id") @PathVariable() Long productId,
                            @ApiParam(name = "product", value = "Product data", required = true) @RequestBody Product product) {
        return productService.updateProductInStore(storeId, productId, product);
    }

    @ApiOperation(value = "Deletes a product from a store given an id")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Product successfully deleted"),
        @ApiResponse(code = 404, message = "Store or product can't be found by id"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{storeId:\\d+}/{productId:\\d+}"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@ApiParam(name = "storeId", value = "Store id") @PathVariable() Long storeId,
                            @ApiParam(name = "productId", value = "Product id") @PathVariable() Long productId) {
        productService.deleteProduct(storeId, productId);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ValidationError error = new ValidationError(ex, null, validationsConverter.getDataIntegrityErrors(ex));
        return new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
    }
}
