package ru.buz.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("client")
public class Client implements Cloneable {
    @Id
    @Column(value = "client_id")
    private Long id;

    private String name;

    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phoneList;

    @Column(value = "address_id")
    private Address address;


    public Client() {
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long client_id, String name) {
        this.id = client_id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, Set<Phone> phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneList = phone;
    }

    public Client(Long client_id, String name, Address address) {
        this.id = client_id;
        this.name = name;
        this.address = address;
    }

    @Override
    public Client clone() {
        Set<Phone> copyPhone = new HashSet<>(phoneList);
        Address addressCopy = new Address(address.getId(), address.getAddress());
        return new Client(this.id, this.name, addressCopy,copyPhone);
    }

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
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Phone> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(Set<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (phoneList != null) {
            for (Phone phone : phoneList) {
                builder.append(phone.getClientPhone()).append(" ");
            }
        }

        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phones=" + builder +
                ", studentAddress=" + address.getAddress() +
                '}';
    }
}
