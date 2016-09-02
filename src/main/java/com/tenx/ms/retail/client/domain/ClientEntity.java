package com.tenx.ms.retail.client.domain;

import com.tenx.ms.commons.validation.constraints.Email;
import com.tenx.ms.commons.validation.constraints.PhoneNumber;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by goropeza on 28/08/16.
 */
@Entity
@Table(name = "client", uniqueConstraints = @UniqueConstraint(name = "unique_client_email", columnNames = {"email"}))
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Email
    @Size(min = 1, max = 50)
    @Column(name = "email", nullable = false)
    @NotNull
    private String email;

    @Pattern(regexp="[a-zA-Z]*")
    @Size(min = 1, max = 50)
    @Column(name = "first_name", nullable = false)
    @NotNull
    private String firstName;

    @Pattern(regexp="[a-zA-Z]*")
    @Size(min = 1, max = 50)
    @Column(name = "last_name", nullable = false)
    @NotNull
    private String lastName;

    @PhoneNumber
    @Size(min = 1, max = 10)
    @Column(name = "phone", nullable = false)
    @NotNull
    private String phone;

    public ClientEntity() {}

    public ClientEntity(String email, String firstName, String lastName, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
