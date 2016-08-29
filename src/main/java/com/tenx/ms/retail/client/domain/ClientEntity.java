package com.tenx.ms.retail.client.domain;

import com.tenx.ms.commons.validation.constraints.Email;
import com.tenx.ms.commons.validation.constraints.PhoneNumber;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by goropeza on 28/08/16.
 */
@Entity
@Table(name = "client")
public class ClientEntity {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public ClientEntity() {}

    public ClientEntity(String email, String firstName, String lastName, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "client_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Email
    @Size(min = 1, max = 50)
    @Column(name = "client_email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Pattern(regexp="[a-zA-Z]*")
    @Size(min = 1, max = 50)
    @Column(name = "client_first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Pattern(regexp="[a-zA-Z]*")
    @Size(min = 1, max = 50)
    @Column(name = "client_last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @PhoneNumber
    @Size(min = 1, max = 10)
    @Column(name = "client_phone", nullable = false)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
