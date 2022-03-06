package ru.buz.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("phone")
public class Phone implements Cloneable{
    @Id
    @Column(value = "phone_id")
    private Long id;

    @Column(value = "client_phone")
    private String clientPhone;




    public Phone(Long id, String phone) {
        this.id = id;
        this.clientPhone = phone;
    }

    public Phone(String phone) {
        this.clientPhone = phone;
    }

    public Phone() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    @Override
    public Phone clone(){
        return new Phone(this.id,this.clientPhone);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "phoneId=" + id +
                ", phone='" + clientPhone + '\'' +
                '}';
    }
}
