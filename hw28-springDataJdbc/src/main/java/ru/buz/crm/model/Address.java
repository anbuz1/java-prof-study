package ru.buz.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
public class Address {
    @Id
    @Column(value = "address_id")
    private Long id;

    private String clientAddress;

    public Address(Long id, String address) {
        this.id = id;

        this.clientAddress = address;
    }

    public Address(String address) {
        this.clientAddress = address;
    }

    public Address() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return clientAddress;
    }

    public void setAddress(String address) {
        this.clientAddress = address;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + id +
                ", address='" + clientAddress + '\'' +
                '}';
    }
}
