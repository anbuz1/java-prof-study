package ru.buz.core.localcache.implementations;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.buz.core.localcache.anotations.CacheId;
import ru.buz.core.localcache.anotations.Cacheable;
import ru.buz.core.localcache.exceptions.PutInCacheException;
import ru.buz.core.localcache.interfaces.BuzCache;
import ru.buz.crm.model.Address;
import ru.buz.crm.model.Client;
import ru.buz.crm.model.Phone;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.buz.core.localcache.implementations.CacheManager.getBuzCacheInstance;

class BuzCacheImplTest {


    private BuzCache buzCache;

    private ClientMock testClient;
    private ClientMock extraClient;
    private Address testAddress;
    private List<ClientMock> clientList;
    private List<Phone> phones;


    @BeforeEach
    void initialize() {
        buzCache = getBuzCacheInstance(Phone.class, ClientMock.class, Address.class);
        clientList = new ArrayList<>();
        phones = List.of(new Phone(1L, "13-555-22"), new Phone(2L, "13-555-23"));

        for (int i = 1; i <= 500; i++) {
            Address tempAddress = new Address((long) i, "AnyStreet");

            ClientMock tempClient = new ClientMock((long) i, "Vasili_" + i,
                    tempAddress,
                    phones);
            clientList.add(tempClient);
            if (i == 201) {
                testClient = tempClient;
                testAddress = tempAddress;
            }
        }
        extraClient = new ClientMock((long) 501, "Vasili_" + 501,
                new Address((long) 501, "AnyStreet"),
                List.of(new Phone(null, "13-555-22"), new Phone(null, "13-555-23")));

    }

    @AfterEach
    void clearCache() {
        buzCache.clearCache();
    }

    @Test
    void testAddAndUpdate() throws PutInCacheException {

        for (int i = 0; i < clientList.size(); i++) {
            ClientMock client = clientList.get(i);
            if (i < 250) {
                buzCache.add(client);
            } else {
                buzCache.add(client.getId(), client);
            }
        }


        assertThat(buzCache.size(ClientMock.class)).isEqualTo(500);

        assertThrows(PutInCacheException.class, () -> {
            buzCache.add(501, extraClient);
        });

        assertThat(buzCache.size(ClientMock.class)).isEqualTo(500);

        testClient.setName("Test");

        buzCache.add(testClient);

        assertThat(buzCache.size(ClientMock.class)).isEqualTo(500);

        assertThat(buzCache.get(201, ClientMock.class).get()).isEqualTo(testClient);


    }


    @Test
    void testGet() throws PutInCacheException {

        for (ClientMock client : clientList) {
            buzCache.add(client);
        }

        assertThat(buzCache.get(201, ClientMock.class).get()).isEqualTo(testClient);

        assertThat(buzCache.get("studentAddress", testAddress, ClientMock.class).size()).isEqualTo(1);

        assertThat(buzCache.get("studentAddress", testAddress, ClientMock.class).get(0)).isEqualTo(testClient);

        assertThat(buzCache.get("phoneList", phones, ClientMock.class).size()).isEqualTo(500);

    }


    @Test
    void testDelete() throws PutInCacheException {

        for (ClientMock client : clientList) {
            buzCache.add(client);
        }

        assertThat(buzCache.size(ClientMock.class)).isEqualTo(500);

        buzCache.delete(201, ClientMock.class);

        assertThat(buzCache.size(ClientMock.class)).isEqualTo(499);

        assertThat(buzCache.get(201, ClientMock.class).isPresent()).isEqualTo(false);
    }

    @Cacheable(cacheSize = 500)
    private class ClientMock {
        private Long id;

        private String name;

        private List<Phone> phoneList;

        private Address studentAddress;


        public ClientMock() {
        }

        public ClientMock(String name) {
            this.id = null;
            this.name = name;
        }

        public ClientMock(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public ClientMock(Long id, String name, Address address, List<Phone> phone) {
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

}