package com.tenx.ms.retail.client.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.client.rest.dto.Client;
import com.tenx.ms.retail.client.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
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

/**
 * Created by goropeza on 30/08/16.
 */
@Api(value = "client", description = "Client API")
@RestController("clientControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @ApiOperation(value = "Finds a client by client id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Client returned successfully"),
        @ApiResponse(code = 404, message = "Client couldn't be found"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{clientId:\\d+}"}, method = RequestMethod.GET)
    public Client getClientById(@ApiParam(name = "clientId", value = "Client id") @PathVariable() Long clientId) {
        return clientService.findClientById(clientId);
    }

    @ApiOperation(value = "Returns all clients")
    @ApiResponses( value = {
        @ApiResponse(code = 200, message = "Client list returned successfully"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<Client> getClients(){
        return clientService.findAllClients();
    }

    @ApiOperation(value = "Creates a new client")
    @ApiResponses( value = {
        @ApiResponse(code = 201, message = "Client successfully created"),
        @ApiResponse(code = 400, message = "Invalid parameters"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCreated<Long> createClient(@ApiParam(name = "client", value = "Client data", required = true) @RequestBody Client client){
        Client newClient = clientService.insertClient(client);
        return new ResourceCreated<Long>(newClient.getClientId());
    }

    @ApiOperation(value = "Updates a client given an id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Client successfully updated"),
        @ApiResponse(code = 400, message = "Invalid parameters"),
        @ApiResponse(code = 404, message = "Client can't be found by id"),
        @ApiResponse(code = 412, message = "Validation failure"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{clientId:\\d+}"}, method = RequestMethod.PUT)
    public Client updateClient(@ApiParam(name = "clientId", value = "Client id") @PathVariable() Long clientId,
                             @ApiParam(name = "client", value = "Client data", required = true) @RequestBody Client client) {
        return clientService.updateClient(clientId, client);
    }

    @ApiOperation(value = "Deletes a client given an id")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Client successfully deleted"),
        @ApiResponse(code = 404, message = "Client can't be found by id"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    @RequestMapping(value = {"/{clientId:\\d+}"}, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@ApiParam(name = "clientId", value = "Client id") @PathVariable() Long clientId) {
        clientService.deleteClient(clientId);
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

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(TransactionSystemException.class)
    protected void handleTransactionSystemException(TransactionSystemException ex,
                                                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
    }
}
