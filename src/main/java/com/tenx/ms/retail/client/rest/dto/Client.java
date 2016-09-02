package com.tenx.ms.retail.client.rest.dto;

import com.tenx.ms.commons.validation.constraints.Email;
import com.tenx.ms.commons.validation.constraints.PhoneNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by goropeza on 30/08/16.
 */
@ApiModel("Client")
public class Client {
    @ApiModelProperty(value = "Client Id", readOnly = true)
    private Long clientId;

    @NotNull
    @Pattern(regexp="[a-zA-Z]*")
    @Size(min = 1, max = 50)
    @ApiModelProperty(value = "Client's First Name", required = true)
    private String firstName;

    @NotNull
    @Pattern(regexp="[a-zA-Z]*")
    @Size(min = 1, max = 50)
    @ApiModelProperty(value = "Client's Last Name", required = true)
    private String lastName;

    @NotNull
    @Email
    @ApiModelProperty(value = "Client's email", required = true)
    private String email;

    @NotNull
    @PhoneNumber
    @ApiModelProperty(value = "Client's phone", required = true)
    private String phone;

    public Client() {}

    public Client(Long clientId, String firstName, String lastName, String email, String phone) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
