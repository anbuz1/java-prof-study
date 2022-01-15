package ru.buz.crm.model;

import javax.persistence.*;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "address")
    String address;

    public Address(Long id, String address) {
        this.id = id;
        this.address = address;
    }

    public Address() {
    }

}
