package ru.buz.crm.model;

import javax.persistence.*;

@Entity
@Table(name = "phone")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone")
    String phone;


    public Phone(Long id, String phone) {
        this.id = id;
        this.phone = phone;
    }

    public Phone() {
    }
}
