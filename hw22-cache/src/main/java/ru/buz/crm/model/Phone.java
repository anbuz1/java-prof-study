package ru.buz.crm.model;

import ru.buz.core.localcache.anotations.CacheId;
import ru.buz.core.localcache.anotations.Cacheable;

import javax.persistence.*;

@Cacheable(cacheSize = 500)
@Entity
@Table(name = "phone",uniqueConstraints = {
        @UniqueConstraint(columnNames = "PHONE_ID")})
public class Phone implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PHONE_ID", unique = true, nullable = false)
    private Long phoneId;

    @Column(name = "STUDENT_PHONE", length = 100)
    private String phone;

    public Phone(Long id, String phone) {
        this.phoneId = id;
        this.phone = phone;
    }

    public Phone() {
    }

    @CacheId
    public Long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public Phone clone(){
        return new Phone(this.phoneId,this.phone);
    }

}
