package ru.buz.crm.model;


import ru.buz.core.localcache.anotations.CacheId;
import ru.buz.core.localcache.anotations.Cacheable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Cacheable(cacheSize = 100000)
@Entity
@Table(name = "client", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID"),
        @UniqueConstraint(columnNames = "ADDRESS_ID")})
public class Client implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CLIENT_ID", nullable = false, updatable = false)
    private List<Phone> phoneList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID", unique = true)
    private Address studentAddress;


    public Client() {
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phone) {
        this.id = id;
        this.name = name;
        this.studentAddress = address;
        this.phoneList = phone;
    }

    @Override
    public Client clone() {
        List<Phone> copyPhone = new ArrayList<>(phoneList);

        Address addressCopy = new Address(studentAddress.getAddressId(),studentAddress.getAddress());
        return new Client(this.id, this.name,addressCopy,copyPhone);
    }
    @CacheId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return studentAddress;
    }

    public void setAddress(Address address) {
        this.studentAddress = address;
    }

    public List<Phone> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Phone phone : phoneList) {
            builder.append(phone.getPhone()).append(" ");
        }

        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phones=" +  builder +
                ", studentAddress=" + studentAddress.getAddress() +
                '}';
    }
}
