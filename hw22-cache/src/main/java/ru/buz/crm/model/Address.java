package ru.buz.crm.model;

import ru.buz.core.localcache.anotations.CacheId;
import ru.buz.core.localcache.anotations.Cacheable;

import javax.persistence.*;

@Cacheable(cacheSize = 500)
@Entity
@Table(name = "address", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ADDRESS_ID")})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADDRESS_ID", unique = true, nullable = false)
    private Long addressId;

    @Column(name = "STUDENT_ADDRESS")
    private String address;

    public Address(Long id, String address) {
        this.addressId = id;
        this.address = address;
    }

    public Address() {
    }

    @CacheId
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
